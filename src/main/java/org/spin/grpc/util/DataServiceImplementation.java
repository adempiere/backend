package org.spin.grpc.util;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.script.ScriptEngine;

import org.compiere.model.Callout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_AD_Element;
import org.compiere.model.MRule;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.grpc.util.DataServiceGrpc.DataServiceImplBase;
import org.spin.grpc.util.Value.ValueType;

import io.grpc.stub.StreamObserver;

public class DataServiceImplementation extends DataServiceImplBase {
	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(DataServiceImplementation.class);
	
	@Override
	public void requestPO(ValueObjectRequest request, StreamObserver<ValueObject> responseObserver) {
		if(request == null
				|| Util.isEmpty(request.getUuid())
				|| request.getCriteria() == null) {
			log.fine("Object Request Null");
			return;
		}
		log.fine("PO Requested = " + request.getUuid());
		ValueObject.Builder poValue = convertPO(request);
		try {
			responseObserver.onNext(poValue.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	@Override
	public void requestCallout(CalloutRequest request, StreamObserver<CalloutResponse> responseObserver) {
		if(request == null) {
			log.fine("Callout Request Null");
			return;
		}
		log.fine("Callout Requested = " + request.getCallout());
		CalloutResponse.Builder calloutResponse = runcallout(request);
		try {
			responseObserver.onNext(calloutResponse.build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
	/**
	 * Request a PO from query
	 * @param request
	 * @return
	 */
	private ValueObject.Builder convertPO(ValueObjectRequest request) {
		//	TODO: From query
		PO entity = new Query(Env.getCtx(), request.getCriteria().getTableName(), I_AD_Element.COLUMNNAME_UUID + " = ?", null)
				.setParameters(request.getUuid())
				.first();
		//	Return
		return convertPO(entity);
	}
	
	/**
	 * Run callout with data from server
	 * @param request
	 * @return
	 */
	private CalloutResponse.Builder runcallout(CalloutRequest request) {
		CalloutResponse.Builder calloutBuilder = CalloutResponse.newBuilder();
		//	TODO: GridTab and GridField bust be instanced
		String result = processCallout(null, null);
		calloutBuilder.setResult(validateNull(result));
		return calloutBuilder;
	}
	
	/**
	 * Process Callout
	 * @param gridTab
	 * @param field
	 * @return
	 */
	private String processCallout (GridTab gridTab, GridField field) {
		String callout = field.getCallout();
		if (callout.length() == 0)
			return "";
		//
		int windowNo = 0;
		Object value = field.getValue();
		Object oldValue = field.getOldValue();
		log.fine(field.getColumnName() + "=" + value
			+ " (" + callout + ") - old=" + oldValue);

		StringTokenizer st = new StringTokenizer(callout, ";,", false);
		while (st.hasMoreTokens()) {
			String cmd = st.nextToken().trim();
			String retValue = "";
			// FR [1877902]
			// CarlosRuiz - globalqss - implement beanshell callout
			// Victor Perez  - vpj-cd implement JSR 223 Scripting
			if (cmd.toLowerCase().startsWith(MRule.SCRIPT_PREFIX)) {
				
				MRule rule = MRule.get(Env.getCtx(), cmd.substring(MRule.SCRIPT_PREFIX.length()));
				if (rule == null) {
					retValue = "Callout " + cmd + " not found"; 
					log.log(Level.SEVERE, retValue);
					return retValue;
				}
				if ( !  (rule.getEventType().equals(MRule.EVENTTYPE_Callout) 
					  && rule.getRuleType().equals(MRule.RULETYPE_JSR223ScriptingAPIs))) {
					retValue = "Callout " + cmd
						+ " must be of type JSR 223 and event Callout"; 
					log.log(Level.SEVERE, retValue);
					return retValue;
				}

				ScriptEngine engine = rule.getScriptEngine();

				// Window context are    W_
				// Login context  are    G_
				MRule.setContext(engine, Env.getCtx(), windowNo);
				// now add the callout parameters windowNo, tab, field, value, oldValue to the engine 
				// Method arguments context are A_
				engine.put(MRule.ARGUMENTS_PREFIX + "WindowNo", windowNo);
				engine.put(MRule.ARGUMENTS_PREFIX + "Tab", this);
				engine.put(MRule.ARGUMENTS_PREFIX + "Field", field);
				engine.put(MRule.ARGUMENTS_PREFIX + "Value", value);
				engine.put(MRule.ARGUMENTS_PREFIX + "OldValue", oldValue);
				engine.put(MRule.ARGUMENTS_PREFIX + "Ctx", Env.getCtx());

				try  {
					retValue = engine.eval(rule.getScript()).toString();
				} catch (Exception e) {
					log.log(Level.SEVERE, "", e);
					retValue = 	"Callout Invalid: " + e.toString();
					return retValue;
				}
			} else {
				Callout call = null;
				String method = null;
				int methodStart = cmd.lastIndexOf('.');
				try {
					if (methodStart != -1) {
						Class<?> cClass = Class.forName(cmd.substring(0,methodStart));
						call = (Callout)cClass.newInstance();
						method = cmd.substring(methodStart+1);
					}
				} catch (Exception e) {
					log.log(Level.SEVERE, "class", e);
					return "Callout Invalid: " + cmd + " (" + e.toString() + ")";
				}

				if (call == null || method == null || method.length() == 0)
					return "Callout Invalid: " + method;

				try {
					retValue = call.start(Env.getCtx(), method, windowNo, gridTab, field, value, oldValue);
				} catch (Exception e) {
					log.log(Level.SEVERE, "start", e);
					retValue = 	"Callout Invalid: " + e.toString();
					return retValue;
				}
				
			}			
			if (!Util.isEmpty(retValue)) {	//	interrupt on first error
				log.severe (retValue);
				return retValue;
			}
		}   //  for each callout
		return "";
	}	//	processCallout
	
	/**
	 * Convert PO to Value Object
	 * @param entity
	 * @return
	 */
	private ValueObject.Builder convertPO(PO entity) {
		ValueObject.Builder builder = ValueObject.newBuilder()
				.setUuid(validateNull(entity.get_ValueAsString(I_AD_Element.COLUMNNAME_UUID)))
				.setId(entity.get_ID());
		//	Convert attributes
		POInfo poInfo = POInfo.getPOInfo(Env.getCtx(), entity.get_Table_ID());
		for(int index = 0; index < poInfo.getColumnCount(); index++) {
			String columnName = poInfo.getColumnName(index);
			Object value = entity.get_Value(index);
			if(value == null) {
				continue;
			}
			Value.Builder builderValue = Value.newBuilder();
			Class<?> clazz = poInfo.getColumnClass(index);
			if (clazz == BigDecimal.class) {
				BigDecimal bigdecimalValue = (BigDecimal) value;
				builderValue.setValueType(ValueType.DOUBLE);
				builderValue.setDoubleValue(bigdecimalValue.doubleValue());
			} else if (clazz == Integer.class) {
				builderValue.setValueType(ValueType.INTEGER);
				builderValue.setIntValue(entity.get_ValueAsInt(index));
			} else if (clazz == String.class) {
				builderValue.setValueType(ValueType.STRING);
				builderValue.setStringValue(validateNull(entity.get_ValueAsString(columnName)));
			} else if (clazz == Boolean.class) {
				builderValue.setValueType(ValueType.BOOLEAN);
				builderValue.setBooleanValue(entity.get_ValueAsBoolean(columnName));
			} else {
				continue;
			}
			//	TODO: Timestamp support
			//	Add
			builder.putValues(columnName, builderValue.build());
		}
		//	
		return builder;
	}
	
	/**
	 * Convert null on ""
	 * @param value
	 * @return
	 */
	private String validateNull(String value) {
		if(value == null) {
			value = "";
		}
		//	
		return value;
	}
}
