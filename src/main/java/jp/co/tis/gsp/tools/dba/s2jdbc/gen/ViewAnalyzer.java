/*
 * Copyright (C) 2015 coastland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.tis.gsp.tools.dba.s2jdbc.gen;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.lang.StringUtils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;

public class ViewAnalyzer implements SelectVisitor, SelectItemVisitor,
		FromItemVisitor {
	private String tableName;
	private List<String> simpleColumnNames;
	private Map<String, String> aliases;
	private boolean isSimple = true;
	private CCJSqlParserManager pm;

	public ViewAnalyzer() {
		pm = new CCJSqlParserManager();
		simpleColumnNames = new ArrayList<String>();
		aliases = new HashMap<String, String>();
	}

	public void parse(String sql) {
		isSimple = true;
		tableName = null;
		try {
			net.sf.jsqlparser.statement.Statement statement = pm
					.parse(new StringReader(sql));
			if (statement instanceof Select) {
				Select selectStatement = (Select) statement;
				visit(selectStatement);
			} else if (statement instanceof CreateView) {
				CreateView createViewStatement = (CreateView) statement;
				this.parse(createViewStatement.getSelectBody().toString());
			}
		} catch (JSQLParserException e) {
			System.err.println("VIEW定義をパース出来ませんでした");
		}
	}

	public boolean isSimple() {
		return isSimple;
	}

	public String getTableName() {
		// MySQLではバッククォートで囲まれるので取り除く
		return StringUtils.strip(tableName, "`");
	}

	public String getAlias(String columnName) {
		return aliases.get(columnName);
	}
	@SuppressWarnings("unchecked")
	public List<String> getColumnNames() {
		return UnmodifiableList.decorate(simpleColumnNames);
	}
	public void visit(Select select) {
		select.getSelectBody().accept(this);
	}

	@Override
	public void visit(Table table) {
		tableName = table.getName();
	}

	@Override
	public void visit(SubSelect subSelect) {
		isSimple = false;
	}

	@Override
	public void visit(SubJoin arg0) {
		isSimple = false;
	}

	@Override
	public void visit(AllColumns arg0) {
	}

	@Override
	public void visit(AllTableColumns arg0) {
	}

	@Override
	public void visit(SelectExpressionItem item) {
		ColumnParser columnParser = new ColumnParser();
		item.getExpression().accept(columnParser);
		if (columnParser.isSimple()) {
			String columnName = columnParser.getColumnName();
			simpleColumnNames.add(columnName);
			aliases.put(columnName, StringUtils.strip(item.getAlias().getName(), "`").toUpperCase());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(PlainSelect select) {
		if (!CollectionUtils.isEmpty(select.getGroupByColumnReferences())
			|| !CollectionUtils.isEmpty(select.getJoins())) {
			isSimple = false;
		} else {
			select.getFromItem().accept(this);
			for (SelectItem item : (List<SelectItem>) select.getSelectItems()) {
				item.accept(this);
			}
		}
	}

	@Override
	public void visit(LateralSubSelect arg0) {
		isSimple = false;
	}

	@Override
	public void visit(ValuesList arg0) {
	}

	@Override
	public void visit(SetOperationList arg0) {
		isSimple = false;
	}

	@Override
	public void visit(WithItem arg0) {
		isSimple = false;
	}

	public static class ColumnParser implements ExpressionVisitor {
		private String columnName = null;
		private boolean isSimple = true;

		public boolean isSimple() {
			return isSimple;
		}

		public String getColumnName() {
			return columnName;
		}
		@Override
		public void visit(NullValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Function arg0) {
			isSimple = false;
		}

		@Override
		public void visit(JdbcParameter arg0) {
			isSimple = false;
		}

		@Override
		public void visit(DoubleValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(LongValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(DateValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(TimeValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(TimestampValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Parenthesis arg0) {
			isSimple = false;
		}

		@Override
		public void visit(StringValue arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Addition arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Division arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Multiplication arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Subtraction arg0) {
			isSimple = false;
		}

		@Override
		public void visit(AndExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(OrExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Between arg0) {
			isSimple = false;
		}

		@Override
		public void visit(EqualsTo arg0) {
			isSimple = false;
		}

		@Override
		public void visit(GreaterThan arg0) {
			isSimple = false;
		}

		@Override
		public void visit(GreaterThanEquals arg0) {
			isSimple = false;
		}

		@Override
		public void visit(InExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(IsNullExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(LikeExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(MinorThan arg0) {
			isSimple = false;
		}

		@Override
		public void visit(MinorThanEquals arg0) {
			isSimple = false;
		}

		@Override
		public void visit(NotEqualsTo arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Column column) {
			columnName = StringUtils.strip(column.getColumnName(), "`").toUpperCase();
		}

		@Override
		public void visit(SubSelect arg0) {
			isSimple = false;
		}

		@Override
		public void visit(CaseExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(WhenClause arg0) {
			isSimple = false;
		}

		@Override
		public void visit(ExistsExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(AllComparisonExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(AnyComparisonExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Concat arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Matches arg0) {
			isSimple = false;
		}

		@Override
		public void visit(BitwiseAnd arg0) {
			isSimple = false;
		}

		@Override
		public void visit(BitwiseOr arg0) {
			isSimple = false;
		}

		@Override
		public void visit(BitwiseXor arg0) {
			isSimple = false;
		}

		@Override
		public void visit(JdbcNamedParameter arg0) {
			isSimple = false;
		}

		@Override
		public void visit(CastExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(Modulo arg0) {
			isSimple = false;
		}

		@Override
		public void visit(AnalyticExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(ExtractExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(IntervalExpression arg0) {
			isSimple = false;
		}

		@Override
		public void visit(SignedExpression arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(HexValue arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(WithinGroupExpression arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(OracleHierarchicalExpression arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(RegExpMatchOperator arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(JsonExpression arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(RegExpMySQLOperator arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(UserVariable arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(NumericBind arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(KeepExpression arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(MySQLGroupConcat arg0) {
			isSimple = false;
			
		}

		@Override
		public void visit(RowConstructor arg0) {
			isSimple = false;
			
		}
	}
}
