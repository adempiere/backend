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
package org.spin.grpc.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;
import org.spin.grpc.util.Value.ValueType;

/**
 * Class for handle Values from and to client
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class ValueUtil {
	
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
}
