/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * This program is free software; you can redistribute it and/or modify it    		 *
 * under the terms version 2 or later of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope   		 *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 		 *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           		 *
 * See the GNU General Public License for more details.                       		 *
 * You should have received a copy of the GNU General Public License along    		 *
 * with this program; if not, write to the Free Software Foundation, Inc.,    		 *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     		 *
 * For the text or an alternative of this public license, you may reach us    		 *
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpya.com				  		                 *
 *************************************************************************************/
package org.spin.base.util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MQuery;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;
import org.spin.grpc.util.Criteria;
import org.spin.grpc.util.Decimal;
import org.spin.grpc.util.KeyValue;
import org.spin.grpc.util.Value;
import org.spin.grpc.util.Condition.Operator;
import org.spin.grpc.util.Value.ValueType;

/**
 * Class for handle Values from and to client
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class ValueUtil {
	
	/**	Date format	*/
	private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	/**
	 * Get Value 
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromObject(Object value) {
		Value.Builder builderValue = Value.newBuilder();
		if(value == null) {
			return builderValue;
		}
		//	Validate value
		if(value instanceof BigDecimal) {
			return getValueFromDecimal((BigDecimal) value);
		} else if (value instanceof Integer) {
			return getValueFromInteger((Integer)value);
		} else if (value instanceof String) {
			return getValueFromString((String) value);
		} else if (value instanceof Boolean) {
			return getValueFromBoolean((Boolean) value);
		} else if(value instanceof Timestamp) {
			return getValueFromDate((Timestamp) value);
		}
		//	
		return builderValue;
	}
	
	/**
	 * Get value from Integer
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromInteger(Integer value) {
		Value.Builder convertedValue = Value.newBuilder().setValueType(ValueType.INTEGER);
		if(value != null) {
			convertedValue.setIntValue((Integer)value);
		}
		//	default
		return convertedValue;
	}
	
	/**
	 * Get value from a string
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromString(String value) {
		return Value.newBuilder().setStringValue(validateNull(value)).setValueType(ValueType.STRING);
	}
	
	/**
	 * Get value from a boolean value
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromBoolean(boolean value) {
		return Value.newBuilder().setBooleanValue(value).setValueType(ValueType.BOOLEAN);
	}
	
	/**
	 * Get value from a date
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromDate(Timestamp value) {
		return Value.newBuilder().setLongValue(value.getTime()).setValueType(ValueType.DATE);
	}
	
	/**
	 * Get value from big decimal
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromDecimal(BigDecimal value) {
		return Value.newBuilder().setDecimalValue(Decimal.newBuilder().setDecimalValue(value.toPlainString()).setScale(value.scale())).setValueType(ValueType.DECIMAL);
	}
	
	/**
	 * Get decimal from big decimal
	 * @param value
	 * @return
	 */
	public static Decimal.Builder getDecimalFromBigDecimal(BigDecimal value) {
		if(value == null) {
			return Decimal.newBuilder();
		}
		return Decimal.newBuilder().setDecimalValue(value.toPlainString()).setScale(value.scale());
	}
	
	/**
	 * Get Decimal from Value
	 * @param value
	 * @return
	 */
	public static BigDecimal getDecimalFromValue(Value value) {
		if(Util.isEmpty(value.getDecimalValue().getDecimalValue())) {
			return null;
		}
		return new BigDecimal(value.getDecimalValue().getDecimalValue());
	}
	
	/**
	 * Get BigDecimal object from decimal
	 * @param decimalValue
	 * @return
	 */
	public static BigDecimal getBigDecimalFromDecimal(Decimal decimalValue) {
		if(decimalValue == null 
				|| Util.isEmpty(decimalValue.getDecimalValue())) {
			return null;
		}
		return new BigDecimal(decimalValue.getDecimalValue());
	}
	
	/**
	 * Get Date from a value
	 * @param value
	 * @return
	 */
	public static Timestamp getDateFromValue(Value value) {
		if(value.getLongValue() > 0) {
			return new Timestamp(value.getLongValue());
		}
		return null;
	}
	
	/**
	 * Get String from a value
	 * @param value
	 * @param uppercase
	 * @return
	 */
	public static String getStringFromValue(Value value, boolean uppercase) {
		String stringValue = value.getStringValue();
		if(Util.isEmpty(stringValue)) {
			stringValue = null;
		}
		//	To Upper case
		if(uppercase) {
			stringValue = stringValue.toUpperCase();
		}
		return stringValue;
	}
	
	/**
	 * Get String from a value
	 * @param value
	 * @return
	 */
	public static String getStringFromValue(Value value) {
		return getStringFromValue(value, false);
	}
	
	/**
	 * Get integer from a value
	 * @param value
	 * @return
	 */
	public static int getIntegerFromValue(Value value) {
		return value.getIntValue();
	}
	
	/**
	 * Get Boolean from a value
	 * @param value
	 * @return
	 */
	public static boolean getBooleanFromValue(Value value) {
		return value.getBooleanValue();
	}
	
	/**
	 * Get Value from reference
	 * @param value
	 * @param referenceId reference of value
	 * @return
	 */
	public static Value.Builder getValueFromReference(Object value, int referenceId) {
		Value.Builder builderValue = Value.newBuilder();
		if(value == null) {
			return builderValue;
		}
		//	Validate values
		if(isLookup(referenceId)
				|| DisplayType.isID(referenceId)) {
			return getValueFromObject(value);
		} else if(DisplayType.Integer == referenceId) {
			if(value instanceof Integer) {
				return getValueFromInteger((Integer) value);
			} else if(value instanceof BigDecimal) {
				return getValueFromInteger(((BigDecimal) value).intValue());
			} else {
				return getValueFromInteger(null);
			}
		} else if(DisplayType.isNumeric(referenceId)) {
			return getValueFromDecimal((BigDecimal) value);
		} else if(DisplayType.YesNo == referenceId) {
			if(value instanceof String) {
				String stringValue = (String) value;
				value = !Util.isEmpty((String) stringValue) && stringValue.equals("Y");
			}
			return getValueFromBoolean((Boolean) value);
		} else if(DisplayType.isDate(referenceId)) {
			return getValueFromDate((Timestamp) value);
		} else if(DisplayType.isText(referenceId)) {
			return getValueFromString((String) value);
		}
		//	
		return builderValue;
	}
	
	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @return
	 */
	public static Map<String, Object> convertValuesToObjects(List<KeyValue> values) {
		Map<String, Object> convertedValues = new HashMap<>();
		for(KeyValue value : values) {
			convertedValues.put(value.getKey(), getObjectFromValue(value.getValue()));
		}
		//	
		return convertedValues;
	}
	
	/**
	 * Default get value from type
	 * @param valueToConvert
	 * @return
	 */
	public static Object getObjectFromValue(Value valueToConvert) {
		return getObjectFromValue(valueToConvert, false);
	}
	
	/**
	 * Get value from parameter type
	 * @param value
	 * @return
	 */
	public static Object getObjectFromValue(Value value, boolean uppercase) {
		if(value.getValueType().equals(ValueType.BOOLEAN)) {
			return value.getBooleanValue();
		} else if(value.getValueType().equals(ValueType.DECIMAL)) {
			return getDecimalFromValue(value);
		} else if(value.getValueType().equals(ValueType.INTEGER)) {
			return value.getIntValue();
		} else if(value.getValueType().equals(ValueType.STRING)) {
			return getStringFromValue(value, uppercase);
		} else if(value.getValueType().equals(ValueType.DATE)) {
			return getDateFromValue(value);
		}
		return null;
	}
	
	/**
	 * Get Object from value based on reference
	 * @param value
	 * @param referenceId
	 * @return
	 */
	public static Object getObjectFromReference(Value value, int referenceId) {
		if(value == null
				|| value.getValueType().equals(ValueType.UNKNOWN)) {
			return null;
		}
		//	Validate values
		if(isLookup(referenceId)
				|| DisplayType.isID(referenceId)) {
			return getObjectFromValue(value);
		} else if(DisplayType.Integer == referenceId) {
			return getIntegerFromValue(value);
		} else if(DisplayType.isNumeric(referenceId)) {
			return getDecimalFromValue(value);
		} else if(DisplayType.YesNo == referenceId) {
			return getBooleanFromValue(value);
		} else if(DisplayType.isDate(referenceId)) {
			return getDateFromValue(value);
		} else if(DisplayType.isText(referenceId)) {
			return getStringFromValue(value);
		}
		//	
		return null;
	}
	
	/**
	 * Is lookup include location
	 * @param displayType
	 * @return
	 */
	public static boolean isLookup(int displayType) {
		return DisplayType.isLookup(displayType)
				|| DisplayType.Account == displayType
				|| DisplayType.Location == displayType
				|| DisplayType.Locator == displayType
				|| DisplayType.PAttribute == displayType;
	}
	
	/**
	 * Convert null on ""
	 * @param value
	 * @return
	 */
	public static String validateNull(String value) {
		if(value == null) {
			value = "";
		}
		//	
		return value;
	}
	
	/**
	 * Get translation if is necessary
	 * @param object
	 * @param columnName
	 * @return
	 */
	public static String getTranslation(PO object, String columnName) {
		if(object == null) {
			return null;
		}
		if(Language.isBaseLanguage(Env.getAD_Language(Env.getCtx()))) {
			return object.get_ValueAsString(columnName);
		}
		//	
		return object.get_Translation(columnName);
	}
	
	/**
	 * Validate if is numeric
	 * @param value
	 * @return
	 */
	public static boolean isNumeric(String value) {
		if(Util.isEmpty(value)) {
			return false;
		}
		//	
		return value.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	/**
	 * Get Int value from String
	 * @param value
	 * @return
	 */
	public static int getIntegerFromString(String value) {
		Integer integerValue = null;
		try {
			integerValue = Integer.parseInt(value);
		} catch (Exception e) {
			
		}
		if(integerValue == null) {
			return 0;
		}
		return integerValue;
	}
	
	
	/**
	 * Validate if is boolean
	 * @param value
	 * @return
	 */
	public static boolean isBoolean(String value) {
		if(Util.isEmpty(value)) {
			return false;
		}
		//	
		return value.equals("Y") 
				|| value.equals("N") 
				|| value.equals("true") 
				|| value.equals("false");
	}
	
	/**
	 * Validate Date
	 * @param value
	 * @return
	 */
	public static boolean isDate(String value) {
		return getDateFromString(value) != null;
	}
	
	/**
	 * Is BigDecimal
	 * @param value
	 * @return
	 */
	public static boolean isBigDecimal(String value) {
		return getBigDecimalFromString(value) != null;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BigDecimal getBigDecimalFromString(String value) {
		BigDecimal numberValue = null;
		if(Util.isEmpty(value)) {
			return null;
		}
		//	
		try {
			numberValue = new BigDecimal(value);
		} catch (Exception e) {
			
		}
		return numberValue;
	}
	
	/**
	 * Get Date from String
	 * @param value
	 * @return
	 */
	public static Timestamp getDateFromString(String value) {
		if(Util.isEmpty(value)) {
			return null;
		}
		Date date = null;
		try {
			date = DisplayType.getTimestampFormat_Default().parse(value);
		} catch (ParseException e) {
			
		}
		//	Convert
		if(date != null) {
			return new Timestamp(date.getTime());
		}
		return null;
	}
	
	/**
	 * Convert operator from gRPC to SQL
	 * @param gRpcOperator
	 * @return
	 */
	public static String convertOperator(int gRpcOperator) {
		String operator = MQuery.EQUAL;
		switch (gRpcOperator) {
			case Operator.BETWEEN_VALUE:
				operator = MQuery.BETWEEN;
				break;
			case Operator.EQUAL_VALUE:
				operator = MQuery.EQUAL;
				break;
			case Operator.GREATER_EQUAL_VALUE:
				operator = MQuery.GREATER_EQUAL;
				break;
			case Operator.GREATER_VALUE:
				operator = MQuery.GREATER;
				break;
			case Operator.IN_VALUE:
				operator = " IN ";
				break;
			case Operator.LESS_EQUAL_VALUE:
				operator = MQuery.LESS_EQUAL;
				break;
			case Operator.LESS_VALUE:
				operator = MQuery.LESS;
				break;
			case Operator.LIKE_VALUE:
				operator = MQuery.LIKE;
				break;
			case Operator.NOT_EQUAL_VALUE:
				operator = MQuery.NOT_EQUAL;
				break;
			case Operator.NOT_IN_VALUE:
				operator = " NOT IN ";
				break;
			case Operator.NOT_LIKE_VALUE:
				operator = MQuery.NOT_LIKE;
				break;
			case Operator.NOT_NULL_VALUE:
				operator = MQuery.NOT_NULL;
				break;
			case Operator.NULL_VALUE:
				operator = MQuery.NULL;
				break;
			default:
				break;
			}
		return operator;
	}
	
	/**
	 * Set Parameter for Statement from object
	 * @param pstmt
	 * @param value
	 * @param index
	 * @throws SQLException
	 */
	public static void setParameterFromObject(PreparedStatement pstmt, Object value, int index) throws SQLException {
		if(value instanceof Integer) {
			pstmt.setInt(index, (Integer) value);
		} else if(value instanceof Double) {
			pstmt.setDouble(index, (Double) value);
		} else if(value instanceof Long) {
			pstmt.setLong(index, (Long) value);
		} else if(value instanceof BigDecimal) {
			pstmt.setBigDecimal(index, (BigDecimal) value);
		} else if(value instanceof String) {
			pstmt.setString(index, (String) value);
		} else if(value instanceof Timestamp) {
			pstmt.setTimestamp(index, (Timestamp) value);
		} else if(value instanceof Boolean) {
			pstmt.setString(index, ((Boolean) value)? "Y": "N");
		}
	}
	
	/**
	 * Set Parameter for Statement from value
	 * @param pstmt
	 * @param value
	 * @param index
	 * @throws SQLException
	 */
	public static void setParameterFromValue(PreparedStatement pstmt, Value value, int index) throws SQLException {
		if(value.getValueType().equals(ValueType.INTEGER)) {
			pstmt.setInt(index, ValueUtil.getIntegerFromValue(value));
		} else if(value.getValueType().equals(ValueType.DECIMAL)) {
			pstmt.setBigDecimal(index, ValueUtil.getDecimalFromValue(value));
		} else if(value.getValueType().equals(ValueType.STRING)) {
			pstmt.setString(index, ValueUtil.getStringFromValue(value));
		} else if(value.getValueType().equals(ValueType.DATE)) {
			pstmt.setTimestamp(index, ValueUtil.getDateFromValue(value));
		}
	}
	
	/**
	 * Get Where Clause from criteria and dynamic condition
	 * @param criteria
	 * @param params
	 * @return
	 */
	public static String getWhereClauseFromCriteria(Criteria criteria, List<Object> params) {
		StringBuffer whereClause = new StringBuffer();
		if(!Util.isEmpty(criteria.getWhereClause())) {
			whereClause.append("(").append(criteria.getWhereClause()).append(")");
		}
		criteria.getConditionsList().stream()
			.filter(condition -> !Util.isEmpty(condition.getColumnName()))
			.forEach(condition -> {
				if(whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				String colummName = criteria.getTableName() + "." + condition.getColumnName(); 
				//	Open
				whereClause.append("(");
				if(condition.getOperatorValue() == Operator.LIKE_VALUE
						|| condition.getOperatorValue() == Operator.NOT_LIKE_VALUE) {
					colummName = "UPPER(" + colummName + ")";
				}
				//	Add operator
				whereClause.append(colummName).append(convertOperator(condition.getOperatorValue()));
				//	For in or not in
				if(condition.getOperatorValue() == Operator.IN_VALUE
						|| condition.getOperatorValue() == Operator.NOT_IN_VALUE) {
					StringBuffer parameter = new StringBuffer();
					condition.getValuesList().forEach(value -> {
						if(parameter.length() > 0) {
							parameter.append(", ");
						}
						parameter.append("?");
						params.add(ValueUtil.getObjectFromValue(value));
					});
					whereClause.append("(").append(parameter).append(")");
				} else if(condition.getOperatorValue() == Operator.BETWEEN_VALUE) {
					whereClause.append(" ? ").append(" AND ").append(" ?");
					params.add(ValueUtil.getObjectFromValue(condition.getValue()));
					params.add(ValueUtil.getObjectFromValue(condition.getValueTo()));
				} else if(condition.getOperatorValue() == Operator.LIKE_VALUE
						|| condition.getOperatorValue() == Operator.NOT_LIKE_VALUE) {
					whereClause.append("?");
					params.add(ValueUtil.getObjectFromValue(condition.getValue(), true));
				} else if(condition.getOperatorValue() != Operator.NULL_VALUE
						&& condition.getOperatorValue() != Operator.NOT_NULL_VALUE) {
					whereClause.append("?");
					params.add(ValueUtil.getObjectFromValue(condition.getValue()));
				}
				//	Close
				whereClause.append(")");
		});
		//	Return where clause
		return whereClause.toString();
	}
	
	/**
	 * Convert string to dates
	 * @param date
	 * @return
	 */
	public static Timestamp convertStringToDate(String date) {
		if(Util.isEmpty(date)) {
			return null;
		}
		SimpleDateFormat dateConverter = new SimpleDateFormat(DATE_FORMAT);
		try {
			Date validFromParameter = dateConverter.parse(date);
			return new Timestamp(validFromParameter.getTime());
		} catch (Exception e) {
			throw new AdempiereException(e);
		}
	}
	
	/**
	 * Convert Timestamp to String
	 * @param date
	 * @return
	 */
	public static String convertDateToString(Timestamp date) {
		if(date == null) {
			return null;
		}
		return new SimpleDateFormat(DATE_FORMAT).format(date);
	}

}
