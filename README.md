# ADempiere gRPC Server
ADempiere gRPC Server example of integration

This is a project for publish Application Dictionary of ADempiere over gRPC service

## Recreate proto stub class
For recreate stub class you must have follow:
- [protobuf](https://github.com/protocolbuffers/protobuf/releases)
- [gradle](https://gradle.org/install)
- Maven
- Also you can see it: [gRPC-Java](https://grpc.io/docs/quickstart/java.html)
- [Complete Java Documentation](https://grpc.io/docs/tutorials/basic/java.html)
After installed it just go to source code folder an run it:

## Init project
``` bash
gradle wrapper
```

## Clean
``` bash
./gradlew clean
```

## Generate Stups
``` bash
./gradlew installDist
```

## Runing Server
The server can be running as java class. See it: **org.spin.grpc.util.DictionaryServer**
Don't forgive set enviroment for ADempiere connection at server run.

## Client Test
The client for testing was writed for java and is located on **org.spin.grpc.util.DictionaryClient**
Just run it and see terminal

## Output
A output generated from client request is the follow:
The source code for generate output is here:
<pre>
public static void main(String[] args) throws Exception {
	DictionaryClient client = new DictionaryClient("localhost", 50051);
	try {
		logger.info("####################### Menu Only #####################");
	    client.requestMenu(false);
	    logger.info("####################### Menu + Child #####################");
	    client.requestMenu(true);
	    logger.info("####################### Window Only #####################");
	    client.requestWindow(false);
	    logger.info("####################### Window + Tabs #####################");
	    client.requestWindow(true);
	    logger.info("####################### Tab Only #####################");
	    client.requestTab(false);
	    logger.info("####################### Tab + Fields #####################");
	    client.requestTab(true);
	    logger.info("####################### Field Only #####################");
	    client.requestField();
	} finally {
		client.shutdown();
	}
}
</pre>
A output from terminal
- Request Menu
<pre>
INFORMACIÓN: ####################### Menu Only #####################
ene 24, 2019 5:06:05 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu: uuid: "8e4fd396-fb40-11e8-a479-7a0060f0aa01"
name: "Recursos Humanos"
isSummary: true
</pre>
- Request Menu with child (Sub-Menu)
<pre>
INFORMACIÓN: ####################### Menu + Child #####################
ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu Child: uuid: "8e51156c-fb40-11e8-a479-7a0060f0aa01"
name: "Departamento"
description: "Mantenimiento de departamentos de n\303\263mina"
action: "W"
windowUuid: "a521ec30-fb40-11e8-a479-7a0060f0aa01"

ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu Child: uuid: "8e50122a-fb40-11e8-a479-7a0060f0aa01"
name: "Recruitment Management"
description: "Recruitment Management"
isSummary: true

ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu Child: uuid: "8e5115bc-fb40-11e8-a479-7a0060f0aa01"
name: "Puesto"
description: "Mantenimiento de puestos de n\303\263mina"
action: "W"
windowUuid: "a521bf80-fb40-11e8-a479-7a0060f0aa01"

ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu Child: uuid: "8e5114c2-fb40-11e8-a479-7a0060f0aa01"
name: "Contrato"
description: "Mantenimiento de contratos de n\303\263mina"
action: "W"
windowUuid: "a521be7c-fb40-11e8-a479-7a0060f0aa01"

ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu Child: uuid: "8e51151c-fb40-11e8-a479-7a0060f0aa01"
name: "Empleado"
description: "Mantenimiento de empleados de n\303\263mina"
action: "W"
windowUuid: "a521befe-fb40-11e8-a479-7a0060f0aa01"

ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu Child: uuid: "8e500fbe-fb40-11e8-a479-7a0060f0aa01"
name: "Employee Setup"
description: "Employee Setup"
isSummary: true

ene 24, 2019 5:07:02 PM org.spin.grpc.util.DictionaryClient requestMenu
INFORMACIÓN: Menu: uuid: "8e4fd396-fb40-11e8-a479-7a0060f0aa01"
name: "Recursos Humanos"
isSummary: true
childs {
  uuid: "8e51156c-fb40-11e8-a479-7a0060f0aa01"
  name: "Departamento"
  description: "Mantenimiento de departamentos de n\303\263mina"
  action: "W"
  windowUuid: "a521ec30-fb40-11e8-a479-7a0060f0aa01"
}
childs {
  uuid: "8e50122a-fb40-11e8-a479-7a0060f0aa01"
  name: "Recruitment Management"
  description: "Recruitment Management"
  isSummary: true
}
childs {
  uuid: "8e5115bc-fb40-11e8-a479-7a0060f0aa01"
  name: "Puesto"
  description: "Mantenimiento de puestos de n\303\263mina"
  action: "W"
  windowUuid: "a521bf80-fb40-11e8-a479-7a0060f0aa01"
}
childs {
  uuid: "8e5114c2-fb40-11e8-a479-7a0060f0aa01"
  name: "Contrato"
  description: "Mantenimiento de contratos de n\303\263mina"
  action: "W"
  windowUuid: "a521be7c-fb40-11e8-a479-7a0060f0aa01"
}
childs {
  uuid: "8e51151c-fb40-11e8-a479-7a0060f0aa01"
  name: "Empleado"
  description: "Mantenimiento de empleados de n\303\263mina"
  action: "W"
  windowUuid: "a521befe-fb40-11e8-a479-7a0060f0aa01"
}
childs {
  uuid: "8e500fbe-fb40-11e8-a479-7a0060f0aa01"
  name: "Employee Setup"
  description: "Employee Setup"
  isSummary: true
}
</pre>
- Request Window
<pre>
INFORMACIÓN: ####################### Window Only #####################
ene 24, 2019 5:09:05 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Window: uuid: "a520de12-fb40-11e8-a479-7a0060f0aa01"
name: "Socio del Negocio"
description: "Mantener Socios del Negocio"
help: "Esta ventana permite definir a cualquier ente con el cual se tenga relaci\303\263n de negocios (Socios del Negocio). Esto incluye clientes, proveedores y empleados. Antes de entrar o importar productos, es necesario definir proveedores. La ventana mantiene toda la informaci\303\263n sobre los tercerso y los valores que aqui se definan, ser\303\241n usados en todas las transacciones de documentos de la aplicaci\303\263n."
isSOTrx: true
</pre>
- Request Window with Tabs
<pre>
ene 24, 2019 5:10:05 PM org.spin.grpc.util.DictionaryClient main
INFORMACIÓN: ####################### Window + Tabs #####################
ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49fca34-fb40-11e8-a479-7a0060f0aa01"
name: "Socio del Negocio"
description: "Socio del Negocio"
help: "La pesta\303\261a tercero define las entidades con las cuales una organizaci\303\263n se relaciona para los negocios."
tableName: "C_BPartner"
sequence: 10
isSingleRow: true
isDeleteable: true

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49fb436-fb40-11e8-a479-7a0060f0aa01"
name: "Cliente"
description: "Definir Par\303\241metros del Cliente"
help: "La pesta\303\261a Cliente define un Socio del Negocio quien es un cliente de esta organizaci\303\263n. Si el cuadro de verificaci\303\263n Cliente es seleccionado entonces los campos relacionados a clientes son desplegados."
tableName: "C_BPartner"
sequence: 20
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49fb59e-fb40-11e8-a479-7a0060f0aa01"
name: "Contabilidad Cliente"
description: "Definir Contabilidad del Cliente"
help: "La pesta\303\261a Contabilidad del Cliente define las cuentas por defecto usadas cuando este cliente es seleccionado en una transacci\303\263n de cuentas por cobrar."
tableName: "C_BP_Customer_Acct"
sequence: 30
tabLevel: 2
isSingleRow: true
isInfoTab: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49fb4e0-fb40-11e8-a479-7a0060f0aa01"
name: "Proveedor"
description: "Definir Par\303\241metros del Proveedor"
help: "La pesta\303\261a Proveedor define un tercero que es un proveedor de esta organizaci\303\263n. Si el cuadro de verificaci\303\263n Proveedor esta seleccionado, los campos relacionados a proveedores son desplegados."
tableName: "C_BPartner"
sequence: 40
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49f9fe6-fb40-11e8-a479-7a0060f0aa01"
name: "Contabilidad Proveedor"
description: "Definir Contabilidad del Proveedor"
help: "La pesta\303\261a de Contabilidad del Proveedor define las cuentas por defecto usadas cuando este proveedor es seleccionado en una transacci\303\263n de cuentas por pagar."
tableName: "C_BP_Vendor_Acct"
sequence: 50
tabLevel: 2
isSingleRow: true
isInfoTab: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "e1823cd6-15a1-11e9-9855-225ff6a8ffc9"
name: "Comisi\303\263n de Proveedor"
description: "Comisi\303\263n de Proveedor definida"
tableName: "C_CommissionLine"
sequence: 60
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "Vendor_ID"
parentColumnName: "Vendor_ID"
displayLogic: "@IsVendor@=\'Y\'"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a07ccc-fb40-11e8-a479-7a0060f0aa01"
name: "Empleado"
description: "Definir Par\303\241metros del Empleado"
help: "La pesta\303\261a Empleado define un tercero quien es un empleado de esta organizaci\303\263n. Si el empleado es tambi\303\251n un representante de ventas entonces el cuadro de verificaci\303\263n correspondiente debe ser seleccionado."
tableName: "C_BPartner"
sequence: 70
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49fa0ea-fb40-11e8-a479-7a0060f0aa01"
name: "Contabilidad Empleado"
description: "Definir Contabilidad del Empleado"
help: "La pesta\303\261a de Contabilidad del Empleado define las cuentas por defecto usadas cuando este empleado es seleccionado para transacciones de reembolso de gastos."
tableName: "C_BP_Employee_Acct"
sequence: 80
tabLevel: 2
isSingleRow: true
isInfoTab: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a07d76-fb40-11e8-a479-7a0060f0aa01"
name: "Cuenta Bancaria"
description: "Definir Cuenta Bancaria"
help: "La pesta\303\261a Definir Cuenta Bancaria define la informaci\303\263n bancaria para este tercero. Estos datos son usados para procesar pagos y giros."
tableName: "C_BP_BankAccount"
sequence: 90
tabLevel: 1
isSingleRow: true
isDeleteable: true

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a06ffc-fb40-11e8-a479-7a0060f0aa01"
name: "Localizaci\303\263n"
description: "Definir localizaci\303\263n"
help: "La pesta\303\261a Localizaci\303\263n define la localizaci\303\263n f\303\255sica de un tercero. Un tercero puede tener registros m\303\272ltiples de localizaci\303\263n."
tableName: "C_BPartner_Location"
sequence: 100
tabLevel: 1
isSingleRow: true
isDeleteable: true

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a15b88-fb40-11e8-a479-7a0060f0aa01"
name: "Contacto (Usuario)"
description: "Mantenga a usuario dentro del sistema - interno o del contacto del tercero"
help: "El usuario identifica a un usuario \303\272nico en el sistema. Esto pod\303\255a ser un usuario interno o un contacto del tercero"
tableName: "AD_User"
sequence: 110
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a16434-fb40-11e8-a479-7a0060f0aa01"
name: "Acceso Socio del Negocio"
description: "Acceso del Usuario/Contacto para informaci\303\263n del tercero y recursos"
help: "Si en Nivel de Usuario, \"Acceso Total Socio del Negocio\" No es seleccionado, aqui debe proporcionar acceso expl\303\255citamente."
tableName: "AD_UserBPAccess"
sequence: 120
tabLevel: 2

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a49f8bc8-fb40-11e8-a479-7a0060f0aa01"
name: "Area de Inter\303\251s"
description: "\303\201rea de Inter\303\251s del Contacto del Socio del Negocio"
help: "\303\201rea de Inter\303\251s puede ser usada para las campa\303\261as de mercadeo."
tableName: "R_ContactInterest"
sequence: 130
tabLevel: 2
isDeleteable: true
linkColumnName: "AD_User_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a2ab78-fb40-11e8-a479-7a0060f0aa01"
name: "Price"
description: "Business Partner specific prices"
help: "Prices and discounts specified here will override the selected price list price and discount schema discount."
tableName: "M_BP_Price"
sequence: 140
tabLevel: 1
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "a4a2ef7a-fb40-11e8-a479-7a0060f0aa01"
name: "Memo"
description: "Business Partner Memo and alerts"
help: "Notes on Business Partner"
tableName: "AD_Memo"
sequence: 150
tabLevel: 1
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Tab: uuid: "28f1d3fc-15a6-11e9-a56b-225ff6a8ffc9"
name: "Miembros Por Defecto"
description: "Miembros de proyecto por defecto"
tableName: "UY_BP_ProjectMember"
sequence: 160
tabLevel: 1
isDeleteable: true
linkColumnName: "C_BPartner_ID"

ene 24, 2019 5:10:06 PM org.spin.grpc.util.DictionaryClient requestWindow
INFORMACIÓN: Window: uuid: "a520de12-fb40-11e8-a479-7a0060f0aa01"
name: "Socio del Negocio"
description: "Mantener Socios del Negocio"
help: "Esta ventana permite definir a cualquier ente con el cual se tenga relaci\303\263n de negocios (Socios del Negocio). Esto incluye clientes, proveedores y empleados. Antes de entrar o importar productos, es necesario definir proveedores. La ventana mantiene toda la informaci\303\263n sobre los tercerso y los valores que aqui se definan, ser\303\241n usados en todas las transacciones de documentos de la aplicaci\303\263n."
isSOTrx: true
tabs {
  uuid: "a49fca34-fb40-11e8-a479-7a0060f0aa01"
  name: "Socio del Negocio"
  description: "Socio del Negocio"
  help: "La pesta\303\261a tercero define las entidades con las cuales una organizaci\303\263n se relaciona para los negocios."
  tableName: "C_BPartner"
  sequence: 10
  isSingleRow: true
  isDeleteable: true
}
tabs {
  uuid: "a49fb436-fb40-11e8-a479-7a0060f0aa01"
  name: "Cliente"
  description: "Definir Par\303\241metros del Cliente"
  help: "La pesta\303\261a Cliente define un Socio del Negocio quien es un cliente de esta organizaci\303\263n. Si el cuadro de verificaci\303\263n Cliente es seleccionado entonces los campos relacionados a clientes son desplegados."
  tableName: "C_BPartner"
  sequence: 20
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a49fb59e-fb40-11e8-a479-7a0060f0aa01"
  name: "Contabilidad Cliente"
  description: "Definir Contabilidad del Cliente"
  help: "La pesta\303\261a Contabilidad del Cliente define las cuentas por defecto usadas cuando este cliente es seleccionado en una transacci\303\263n de cuentas por cobrar."
  tableName: "C_BP_Customer_Acct"
  sequence: 30
  tabLevel: 2
  isSingleRow: true
  isInfoTab: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a49fb4e0-fb40-11e8-a479-7a0060f0aa01"
  name: "Proveedor"
  description: "Definir Par\303\241metros del Proveedor"
  help: "La pesta\303\261a Proveedor define un tercero que es un proveedor de esta organizaci\303\263n. Si el cuadro de verificaci\303\263n Proveedor esta seleccionado, los campos relacionados a proveedores son desplegados."
  tableName: "C_BPartner"
  sequence: 40
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a49f9fe6-fb40-11e8-a479-7a0060f0aa01"
  name: "Contabilidad Proveedor"
  description: "Definir Contabilidad del Proveedor"
  help: "La pesta\303\261a de Contabilidad del Proveedor define las cuentas por defecto usadas cuando este proveedor es seleccionado en una transacci\303\263n de cuentas por pagar."
  tableName: "C_BP_Vendor_Acct"
  sequence: 50
  tabLevel: 2
  isSingleRow: true
  isInfoTab: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "e1823cd6-15a1-11e9-9855-225ff6a8ffc9"
  name: "Comisi\303\263n de Proveedor"
  description: "Comisi\303\263n de Proveedor definida"
  tableName: "C_CommissionLine"
  sequence: 60
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
  linkColumnName: "Vendor_ID"
  parentColumnName: "Vendor_ID"
  displayLogic: "@IsVendor@=\'Y\'"
}
tabs {
  uuid: "a4a07ccc-fb40-11e8-a479-7a0060f0aa01"
  name: "Empleado"
  description: "Definir Par\303\241metros del Empleado"
  help: "La pesta\303\261a Empleado define un tercero quien es un empleado de esta organizaci\303\263n. Si el empleado es tambi\303\251n un representante de ventas entonces el cuadro de verificaci\303\263n correspondiente debe ser seleccionado."
  tableName: "C_BPartner"
  sequence: 70
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a49fa0ea-fb40-11e8-a479-7a0060f0aa01"
  name: "Contabilidad Empleado"
  description: "Definir Contabilidad del Empleado"
  help: "La pesta\303\261a de Contabilidad del Empleado define las cuentas por defecto usadas cuando este empleado es seleccionado para transacciones de reembolso de gastos."
  tableName: "C_BP_Employee_Acct"
  sequence: 80
  tabLevel: 2
  isSingleRow: true
  isInfoTab: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a4a07d76-fb40-11e8-a479-7a0060f0aa01"
  name: "Cuenta Bancaria"
  description: "Definir Cuenta Bancaria"
  help: "La pesta\303\261a Definir Cuenta Bancaria define la informaci\303\263n bancaria para este tercero. Estos datos son usados para procesar pagos y giros."
  tableName: "C_BP_BankAccount"
  sequence: 90
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
}
tabs {
  uuid: "a4a06ffc-fb40-11e8-a479-7a0060f0aa01"
  name: "Localizaci\303\263n"
  description: "Definir localizaci\303\263n"
  help: "La pesta\303\261a Localizaci\303\263n define la localizaci\303\263n f\303\255sica de un tercero. Un tercero puede tener registros m\303\272ltiples de localizaci\303\263n."
  tableName: "C_BPartner_Location"
  sequence: 100
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
}
tabs {
  uuid: "a4a15b88-fb40-11e8-a479-7a0060f0aa01"
  name: "Contacto (Usuario)"
  description: "Mantenga a usuario dentro del sistema - interno o del contacto del tercero"
  help: "El usuario identifica a un usuario \303\272nico en el sistema. Esto pod\303\255a ser un usuario interno o un contacto del tercero"
  tableName: "AD_User"
  sequence: 110
  tabLevel: 1
  isSingleRow: true
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a4a16434-fb40-11e8-a479-7a0060f0aa01"
  name: "Acceso Socio del Negocio"
  description: "Acceso del Usuario/Contacto para informaci\303\263n del tercero y recursos"
  help: "Si en Nivel de Usuario, \"Acceso Total Socio del Negocio\" No es seleccionado, aqui debe proporcionar acceso expl\303\255citamente."
  tableName: "AD_UserBPAccess"
  sequence: 120
  tabLevel: 2
}
tabs {
  uuid: "a49f8bc8-fb40-11e8-a479-7a0060f0aa01"
  name: "Area de Inter\303\251s"
  description: "\303\201rea de Inter\303\251s del Contacto del Socio del Negocio"
  help: "\303\201rea de Inter\303\251s puede ser usada para las campa\303\261as de mercadeo."
  tableName: "R_ContactInterest"
  sequence: 130
  tabLevel: 2
  isDeleteable: true
  linkColumnName: "AD_User_ID"
}
tabs {
  uuid: "a4a2ab78-fb40-11e8-a479-7a0060f0aa01"
  name: "Price"
  description: "Business Partner specific prices"
  help: "Prices and discounts specified here will override the selected price list price and discount schema discount."
  tableName: "M_BP_Price"
  sequence: 140
  tabLevel: 1
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "a4a2ef7a-fb40-11e8-a479-7a0060f0aa01"
  name: "Memo"
  description: "Business Partner Memo and alerts"
  help: "Notes on Business Partner"
  tableName: "AD_Memo"
  sequence: 150
  tabLevel: 1
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
tabs {
  uuid: "28f1d3fc-15a6-11e9-a56b-225ff6a8ffc9"
  name: "Miembros Por Defecto"
  description: "Miembros de proyecto por defecto"
  tableName: "UY_BP_ProjectMember"
  sequence: 160
  tabLevel: 1
  isDeleteable: true
  linkColumnName: "C_BPartner_ID"
}
</pre>
- Request Tab
<pre>
ene 24, 2019 5:11:15 PM org.spin.grpc.util.DictionaryClient main
INFORMACIÓN: ####################### Tab Only #####################
ene 24, 2019 5:11:15 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Tab: uuid: "a49fb4e0-fb40-11e8-a479-7a0060f0aa01"
name: "Proveedor"
description: "Definir Par\303\241metros del Proveedor"
help: "La pesta\303\261a Proveedor define un tercero que es un proveedor de esta organizaci\303\263n. Si el cuadro de verificaci\303\263n Proveedor esta seleccionado, los campos relacionados a proveedores son desplegados."
tableName: "C_BPartner"
sequence: 40
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "C_BPartner_ID"
</pre>
- Request Tab and Fields
<pre>
ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient main
INFORMACIÓN: ####################### Tab + Fields #####################
ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d635656-fb40-11e8-a479-7a0060f0aa01"
name: "Grupo de Socio del Negocio"
description: "ID del Grupo de Socio del Negocio"
help: "La ID Grupo del Socio del Negocio proporciona un m\303\251todo de definir valores predeterminados a ser usados para Socios del Negocio individuales."
columnName: "C_BP_Group_ID"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 19

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d60ee02-fb40-11e8-a479-7a0060f0aa01"
name: "Descripci\303\263n de Orden"
description: "Descripci\303\263n a ser usada en \303\263rdenes"
help: "La descripci\303\263n de la orden identifica la descripci\303\263n est\303\241ndar a usar en \303\263rdenes para este cliente"
columnName: "SO_Description"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d36f9f8-fb40-11e8-a479-7a0060f0aa01"
name: "Regla de Facturaci\303\263n"
description: "Frecuencia y m\303\251todos de facturaci\303\263n"
help: "La regla de facturaci\303\263n define c\303\263mo se le factura a un Socio del Negocio y la frecuencia de facturaci\303\263n."
columnName: "InvoiceRule"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 17
reference {
  uuid: "a47db958-fb40-11e8-a479-7a0060f0aa01"
  name: "C_Order InvoiceRule"
  validationType: "L"
  values {
    uuid: "a4553320-fb40-11e8-a479-7a0060f0aa01"
    value: "D"
    name: "Despu\303\251s de Entrega"
    description: "Factura por entrega"
  }
  values {
    uuid: "a45533a2-fb40-11e8-a479-7a0060f0aa01"
    value: "S"
    name: "Programa del cliente despu\303\251s de entrega"
    description: "Factura seg\303\272n el programa de Facturaci\303\263n del Cliente"
  }
  values {
    uuid: "a45540a4-fb40-11e8-a479-7a0060f0aa01"
    value: "I"
    name: "Inmediato"
    description: "Facturaci\303\263n Inmediata"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5f1898-fb40-11e8-a479-7a0060f0aa01"
name: "Regla de Entrega"
description: "Define los tiempos de entrega"
help: "La Regla de Entrega indica cuando una orden debe ser entregada. Por Ej. Si la orden debiera entregarse cuando est\303\241 completa; cuando una partida est\303\241 completa o cuando el producto llega a estar disponible."
columnName: "DeliveryRule"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 17
reference {
  uuid: "a47e7848-fb40-11e8-a479-7a0060f0aa01"
  name: "C_Order DeliveryRule"
  validationType: "L"
  values {
    uuid: "a4553410-fb40-11e8-a479-7a0060f0aa01"
    value: "R"
    name: "Despu\303\251s del cobro"
    description: "Despu\303\251s del cobro"
  }
  values {
    uuid: "a4553eb0-fb40-11e8-a479-7a0060f0aa01"
    value: "A"
    name: "Disponibilidad"
    description: "Tan pronto como el art\303\255culo est\303\251 disponible"
  }
  values {
    uuid: "a4553fa0-fb40-11e8-a479-7a0060f0aa01"
    value: "L"
    name: "L\303\255nea Completa"
    description: "Tan pronto como todos los art\303\255culos de una l\303\255nea est\303\251n disponibles"
  }
  values {
    uuid: "a455402c-fb40-11e8-a479-7a0060f0aa01"
    value: "O"
    name: "Orden Completa"
    description: "Tan pronto como todos los art\303\255culos de una orden est\303\251n disponibles"
  }
  values {
    uuid: "a4587076-fb40-11e8-a479-7a0060f0aa01"
    value: "F"
    name: "Forzado"
  }
  values {
    uuid: "a459d36c-fb40-11e8-a479-7a0060f0aa01"
    value: "M"
    name: "Manual"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d641564-fb40-11e8-a479-7a0060f0aa01"
name: "Agente Comercial"
description: "Agente Comercial"
help: "El Agente comercial indica el Agente comercial para esta regi\303\263n."
columnName: "SalesRep_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 18
reference {
  uuid: "a47e14c0-fb40-11e8-a479-7a0060f0aa01"
  name: "AD_User - SalesRep"
  validationType: "T"
  referenceTable {
    uuid: "2ea6d9b0-1f96-11e9-a86f-07cba138b6eb"
    tableName: "AD_User"
    keyColumnName: "AD_User_ID"
    displayColumnName: "Name"
    whereClause: "EXISTS (SELECT * FROM C_BPartner bp WHERE AD_User.C_BPartner_ID=bp.C_BPartner_ID AND bp.IsSalesRep=\'Y\')\n"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d6135a6-fb40-11e8-a479-7a0060f0aa01"
name: "Regla de Costo de Flete"
description: "M\303\251todo para cargar el flete"
help: "La regla de costo de flete indica el m\303\251todo usado para cargar los fletes."
columnName: "FreightCostRule"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 17
reference {
  uuid: "a47dba7a-fb40-11e8-a479-7a0060f0aa01"
  name: "C_Order FreightCostRule"
  validationType: "L"
  values {
    uuid: "a4554112-fb40-11e8-a479-7a0060f0aa01"
    value: "I"
    name: "Flete Incluido"
    description: "Costo de flete incluido"
  }
  values {
    uuid: "a455572e-fb40-11e8-a479-7a0060f0aa01"
    value: "C"
    name: "Calculado"
    description: "C\303\241lculo basado en la regla de flete del producto"
  }
  values {
    uuid: "a45557a6-fb40-11e8-a479-7a0060f0aa01"
    value: "F"
    name: "Precio Fijo"
    description: "Precio fijo de flete"
  }
  values {
    uuid: "a460c5dc-fb40-11e8-a479-7a0060f0aa01"
    value: "L"
    name: "L\303\255nea"
    description: "Introducido al nivel de l\303\255nea"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5fad4e-fb40-11e8-a479-7a0060f0aa01"
name: "V\303\255a de Entrega"
description: "Como ser\303\241 entregada la orden"
help: "La v\303\255a de entrega indica como el producto deber\303\255a ser entregado. Por Ej. Si la orden ser\303\241 recogida o embarcada."
columnName: "DeliveryViaRule"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 17
reference {
  uuid: "a47dbb42-fb40-11e8-a479-7a0060f0aa01"
  name: "C_Order DeliveryViaRule"
  validationType: "L"
  values {
    uuid: "a4556502-fb40-11e8-a479-7a0060f0aa01"
    value: "D"
    name: "Entrega"
  }
  values {
    uuid: "a455657a-fb40-11e8-a479-7a0060f0aa01"
    value: "S"
    name: "Transportista"
  }
  values {
    uuid: "a459d3d0-fb40-11e8-a479-7a0060f0aa01"
    value: "P"
    name: "Recolecci\303\263n"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d572d7c-fb40-11e8-a479-7a0060f0aa01"
name: "Socio del Negocio"
description: "Identifica un Socio del Negocio"
help: "Un Socio del Negocio es cualquiera con quien usted realiza transacciones. Este puede incluir Proveedores; Clientes; Empleados o Vendedores."
columnName: "C_BPartner_ID"
isDisplayedGrid: true
isMandatory: true
isKey: true
isAllowLogging: true
displayType: 13

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d63cbe0-fb40-11e8-a479-7a0060f0aa01"
name: "Esq List Precios/Desc"
description: "Esquema para calcular el porcentaje de descuento comercial"
help: "Despu\303\251s del c\303\241lculo de precio (est\303\241ndar); el porcentaje de descuento comercial es calculado y aplicado resultando en el precio final"
columnName: "M_DiscountSchema_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 18
reference {
  uuid: "a47daee0-fb40-11e8-a479-7a0060f0aa01"
  name: "M_DiscountSchema not PL"
  validationType: "T"
  referenceTable {
    uuid: "2ea6bcd2-1f96-11e9-a837-7b20558cfdf4"
    tableName: "M_DiscountSchema"
    keyColumnName: "M_DiscountSchema_ID"
    displayColumnName: "Name"
    whereClause: "M_DiscountSchema.DiscountType<>\'P\'"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d60aa50-fb40-11e8-a479-7a0060f0aa01"
name: "Env\303\255a Email"
description: "Permite enviar el documento Email"
help: "Env\303\255e los email con el documento unido (ej. factura, nota de entrega, etc.)"
columnName: "SendEMail"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d60b55e-fb40-11e8-a479-7a0060f0aa01"
name: "Socio del Negocio Padre"
description: "Socio del Negocio Padre"
help: "El padre (organizaci\303\263n) del Socio del Negocio para reportar prop\303\263sitos."
columnName: "BPartner_Parent_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 18
reference {
  uuid: "a47d092c-fb40-11e8-a479-7a0060f0aa01"
  name: "C_BPartner Parent"
  validationType: "T"
  referenceTable {
    uuid: "2ea664f8-1f96-11e9-a7bd-477f3868a0f6"
    tableName: "C_BPartner"
    keyColumnName: "C_BPartner_ID"
    displayColumnName: "Name"
    whereClause: "C_BPartner.IsSummary=\'Y\'"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d60fb7c-fb40-11e8-a479-7a0060f0aa01"
name: "Formato Impresi\303\263n Factura"
description: "Formato de impresi\303\263n usado para imprimir facturas"
help: "Es necesario definir un formato para imprimir el documento"
columnName: "Invoice_PrintFormat_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 18
reference {
  uuid: "a47dfa08-fb40-11e8-a479-7a0060f0aa01"
  name: "AD_PrintFormat Invoice"
  validationType: "T"
  referenceTable {
    uuid: "2ea65f26-1f96-11e9-a7b0-7b837c354c05"
    tableName: "AD_PrintFormat"
    keyColumnName: "AD_PrintFormat_ID"
    displayColumnName: "Name"
    whereClause: "AD_PrintFormat.AD_Table_ID=516"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d6750a8-fb40-11e8-a479-7a0060f0aa01"
name: "Estado del Cr\303\251dito"
description: "Estado del cr\303\251dito de ventas"
help: "Solamente para la documentaci\303\263n."
columnName: "SOCreditStatus"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 17
reference {
  uuid: "a47d457c-fb40-11e8-a479-7a0060f0aa01"
  name: "C_BPartner SOCreditStatus"
  validationType: "L"
  values {
    uuid: "a45858d4-fb40-11e8-a479-7a0060f0aa01"
    value: "S"
    name: "Cr\303\251dito Detenido"
  }
  values {
    uuid: "a4585a0a-fb40-11e8-a479-7a0060f0aa01"
    value: "H"
    name: "Cr\303\251dito Retenido"
  }
  values {
    uuid: "a4585adc-fb40-11e8-a479-7a0060f0aa01"
    value: "W"
    name: "Cr\303\251dito en Verificaci\303\263n"
  }
  values {
    uuid: "a4585b90-fb40-11e8-a479-7a0060f0aa01"
    value: "X"
    name: "Sin Verificaci\303\263n de Cr\303\251dito"
  }
  values {
    uuid: "a4585c12-fb40-11e8-a479-7a0060f0aa01"
    value: "O"
    name: "Cr\303\251dito Correcto"
  }
}

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d64a2ea-fb40-11e8-a479-7a0060f0aa01"
name: "M\303\255n de Vida \303\272til %"
description: "M\303\255nimo de vida \303\272til en porcentaje basados en la fecha lo que garantiza el producto."
help: "M\303\255nimo de vida \303\272til en productos con fecha de garant\303\255a. Si > 0 Usted no puede seleccionar productos con una vida \303\272til. (fecha - dia de la garant\303\255a) / menos que la vida \303\272til del m\303\255nimo, a menos que usted seleccione \"toda demostraci\303\263n\""
columnName: "ShelfLifeMinPct"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 11

ene 24, 2019 5:12:11 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d66d2fe-fb40-11e8-a479-7a0060f0aa01"
name: "Liga Organizaci\303\263n"
description: "Integraci\303\263n de Socio del Negocio a una Organizaci\303\263n"
help: "Si el Socio del Negocio esta en otra organizaci\303\263n, seleccione la organizaci\303\263n o fije para crear una nueva organizaci\303\263n. Usted liga a Socio del Negocio a una organizaci\303\263n para crear los documentos expl\303\255citos para la Integraci\303\263n-Org transacci\303\263n. Si usted crea una nueva organizaci\303\263n, usted puede proveer un tipo de la organizaci\303\263n. Si usted selecciona un rol, el acceso a la nueva organizaci\303\263n se limita a ese rol, si no todo los roles (no manual) del cliente tendr\303\241n acceso a la nueva organizaci\303\263n."
columnName: "AD_OrgBP_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 28
readOnlyLogic: "@AD_OrgBP_ID@!0"
process {
  uuid: "a42b86e2-fb40-11e8-a479-7a0060f0aa01"
  name: "Liga Organizaci\303\263n"
  description: "Integraci\303\263n de Socio del Negocio a una Organizaci\303\263n"
  help: "Si el Socio del Negocio esta en otra organizaci\303\263n, seleccione la organizaci\303\263n o fije para crear una nueva organizaci\303\263n. Usted liga a Socio del Negocio a una organizaci\303\263n para crear los documentos expl\303\255citos para la Integraci\303\263n-Org transacci\303\263n. Si usted crea una nueva organizaci\303\263n, usted puede proveer un tipo de la organizaci\303\263n. Si usted selecciona un rol, el acceso a la nueva organizaci\303\263n se limita a ese rol, si no todo los roles (no manual) del cliente tendr\303\241n acceso a la nueva organizaci\303\263n."
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d36d306-fb40-11e8-a479-7a0060f0aa01"
name: "% Descuento"
description: "Porcentaje de descuento simple"
columnName: "FlatDiscount"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 22

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d62dc3a-fb40-11e8-a479-7a0060f0aa01"
name: "Saldo Actual"
description: "Total de importe en balance abierto, en las cuentas primarias actuales."
help: "La cantidad abierta total del balance es la cantidad abierta calculada del art\303\255culo para la actividad del cliente y del proveedor. Si el equilibrio est\303\241 debajo de cero, debemos al Socio del Negocio. El importe se utiliza para la gerencia de cr\303\251dito. Las facturas y las asignaciones del pago determinan el equilibrio abierto (es decir no las \303\263rdenes o los pagos).\n\nThe Total Open Balance Amount is the calculated open item amount for Customer and Vendor activity.  If the Balance is below zero, we owe the Business Partner.  The amout is used for Credit Management.\n\nInvoices and Payment Allocations determine the Open Balance (i.e. not Orders or Payments)."
columnName: "TotalOpenBalance"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 12

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d64b2b2-fb40-11e8-a479-7a0060f0aa01"
name: "Descripci\303\263n"
description: "Descripci\303\263n corta opcional del registro"
help: "Una descripci\303\263n esta limitada a 255 caracteres"
columnName: "Description"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
isSelectionColumn: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d6006e0-fb40-11e8-a479-7a0060f0aa01"
name: "Volumen de Ventas"
description: "Volumen total de Ventas"
help: "El Volumen de ventas indica el volumen total de ventas para un Socio del Negocio"
columnName: "SalesVolume"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 11

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d64a3a8-fb40-11e8-a479-7a0060f0aa01"
name: "No. de Referencia"
description: "Su n\303\272mero de cliente o proveedor con el Socio del Negocio."
help: "El n\303\272mero de referencia puede ser impreso en \303\263rdenes y facturas para permitirle a su Socio del Negocio identificar m\303\241s r\303\241pido sus registros."
columnName: "ReferenceNo"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d60fc12-fb40-11e8-a479-7a0060f0aa01"
name: "DUNS"
description: "DUNS (del ingl\303\251s Data Universal Numbering System o Sistema Universal de Numeraci\303\263n de Datos)"
help: "Usado por EDI - para detalles ver www.dnb.com/dunsno/list.htm"
columnName: "DUNS"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d64bf50-fb40-11e8-a479-7a0060f0aa01"
name: "Empleados"
description: "N\303\272mero de empleados"
help: "Indica el n\303\272mero de empleados de este Socio del Negocio. Este campo se despliega solamente para prospectos."
columnName: "NumberEmployees"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 11

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5ed874-fb40-11e8-a479-7a0060f0aa01"
name: "N\303\272mero Identificaci\303\263n"
description: "N\303\272mero de Identificaci\303\263n Tributaria"
help: "N\303\272mero de Identificaci\303\263n Tributaria es el n\303\272mero de identificaci\303\263n gubernamental de esta entidad"
columnName: "TaxID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d62a27e-fb40-11e8-a479-7a0060f0aa01"
name: "NAICS/SIC"
description: "Codigo est\303\241ndard de la industria o sucesor NAIC - http://www.osha.gov/oshstats/sicser.html"
help: "El NAICS/SIC identifica cualquiera de esos c\303\263digos que puedan ser aplicables a este Socio del Negocio"
columnName: "NAICS"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d63d07c-fb40-11e8-a479-7a0060f0aa01"
name: "Entidad Acumulada"
description: "Esta es una entidad sumaria"
help: "Una entidad sumaria representa una rama en un \303\241rbol mas bien que un nodo final. Las entidades sumarias son usadas para reportar y no tienen valores propios"
columnName: "IsSummary"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d662b7e-fb40-11e8-a479-7a0060f0aa01"
name: "Lenguaje"
description: "Lenguaje para esta entidad"
help: "El lenguaje identifica el lenguaje a usar para el despliegue"
columnName: "AD_Language"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 18
reference {
  uuid: "a47dd348-fb40-11e8-a479-7a0060f0aa01"
  name: "AD_Language System"
  validationType: "T"
  referenceTable {
    uuid: "2ea635be-1f96-11e9-a7ab-fb7e255c2700"
    tableName: "AD_Language"
    keyColumnName: "AD_Language"
    displayColumnName: "Name"
    whereClause: "(AD_Language.IsSystemLanguage=\'Y\' OR AD_Language.IsBaseLanguage=\'Y\')"
  }
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5fb2c6-fb40-11e8-a479-7a0060f0aa01"
name: "Cliente"
description: "Indica si el Socio del Negocio es un cliente"
help: "El cuadro de verificaci\303\263n cliente indica si el Socio del Negocio es un cliente. Si se seleccionan campos adicionales desplegar\303\241n informaci\303\263n adicional para definir al cliente."
columnName: "IsCustomer"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d61351a-fb40-11e8-a479-7a0060f0aa01"
name: "Programa de Facturaci\303\263n"
description: "Programa para generar facturas"
help: "El programa de facturaci\303\263n identifica la frecuencia usada cuando se generan facturas."
columnName: "C_InvoiceSchedule_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 19

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d630796-fb40-11e8-a479-7a0060f0aa01"
name: "Prospecto Activo"
description: "Indica un prospecto en oposici\303\263n a un cliente activo."
help: "El cuadro de verificaci\303\263n prospecto indica una entidad que es un prospecto activo pero no es a\303\272n un cliente."
columnName: "IsProspect"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d6306e2-fb40-11e8-a479-7a0060f0aa01"
name: "Primera Venta"
description: "Fecha de la primera venta"
help: "La fecha de la Primera Venta indica la fecha de la primera venta a este Socio del Negocio"
columnName: "FirstSale"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 15

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d6749f0-fb40-11e8-a479-7a0060f0aa01"
name: "L\303\255mite de Cr\303\251dito"
description: "Total pendiente del total de la factura pendiente."
help: "El l\303\255mite de cr\303\251dito indica el total de deuda permitida."
columnName: "SO_CreditLimit"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 12

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d627556-fb40-11e8-a479-7a0060f0aa01"
name: "Cr\303\251dito Usado"
description: "Balance actual abierto"
help: "El cr\303\251dito usado indica la cantidad total de facturas abiertas o sin pagar del Socio del Negocio"
columnName: "SO_CreditUsed"
isDisplayedGrid: true
isMandatory: true
isAllowLogging: true
displayType: 12

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5a33dc-fb40-11e8-a479-7a0060f0aa01"
name: "Costo de Adquisici\303\263n"
description: "Costo de ganar el prospecto como cliente"
help: "El costo de adquisici\303\263n identifica el costo asociado con hacer de este prospecto un cliente"
columnName: "AcqusitionCost"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 37

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d62c376-fb40-11e8-a479-7a0060f0aa01"
name: "Valor Esperado"
description: "Total de ingresos esperados"
help: "El valor en el tiempo de vida potencial es el ingreso anticipado a ser generado por este Socio del Negocio."
columnName: "PotentialLifeTimeValue"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 12

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d635f5c-fb40-11e8-a479-7a0060f0aa01"
name: "T\303\251rmino de Pago"
description: "Condiciones de pago de esta transacci\303\263n"
help: "Las condiciones de pago indican el m\303\251todo y tiempo de pago para esta transacci\303\263n."
columnName: "C_PaymentTerm_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 19

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d629568-fb40-11e8-a479-7a0060f0aa01"
name: "Valor Total Transacciones"
description: "Ingreso de tiempo de vida Actual"
help: "El valor de tiempo de vida actual es el ingreso registrado y generado por este Socio del Negocio."
columnName: "ActualLifeTimeValue"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 12

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d629658-fb40-11e8-a479-7a0060f0aa01"
name: "Participaci\303\263n"
description: "Participaci\303\263n del cliente."
help: "La participaci\303\263n indica el porcentaje de este Socio del Negocio."
columnName: "ShareOfCustomer"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 11

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d62b9e4-fb40-11e8-a479-7a0060f0aa01"
name: "Empleado"
description: "Indica si el Socio del Negocio es un empleado"
help: "El cuadro de verificaci\303\263n empleado indica si este Socio del Negocio es un empleado. Si se selecciona se desplegar\303\241n campos adicionales para identificar a este empleado."
columnName: "IsEmployee"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d626462-fb40-11e8-a479-7a0060f0aa01"
name: "Lista de Precios"
description: "Identificador \303\272nico de mi lista de precios"
help: "Listas de precios son usadas para determinar el precio; margen y costo de art\303\255culos comprados o vendidos."
columnName: "M_PriceList_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 19

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5a35a8-fb40-11e8-a479-7a0060f0aa01"
name: "Transacci\303\263n de una vez"
columnName: "IsOneTime"
isDisplayedGrid: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5a3684-fb40-11e8-a479-7a0060f0aa01"
name: "Direcci\303\263n Web"
description: "Direcci\303\263n Web complete - e.g. http://www.globalqss.com"
help: "El URL define una direcci\303\263n web para este Socio del Negocio"
columnName: "URL"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 40

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d60bf86-fb40-11e8-a479-7a0060f0aa01"
name: "Exento de Impuesto en Venta"
description: "Este Socio del Negocio es exento (o no le debo facturar) del impuesto de ventas."
help: "El cuadro de verificaci\303\263n exento de impuesto identifica un Socio del Negocio quien no esta sujeto al impuesto de ventas."
columnName: "IsTaxExempt"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5a34c2-fb40-11e8-a479-7a0060f0aa01"
name: "Valuaci\303\263n ABC"
description: "Clasificaci\303\263n o importancia de un Socio del Negocio."
help: "La valuaci\303\263n es usada para identificar la importancia del Socio del Negocio"
columnName: "Rating"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d62c2c2-fb40-11e8-a479-7a0060f0aa01"
name: "Regla de Pago"
description: "Como se pagar\303\241 la factura"
help: "La Regla de Pagos indica el m\303\251todo de pago de la factura"
columnName: "PaymentRule"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 17
reference {
  uuid: "a47e0606-fb40-11e8-a479-7a0060f0aa01"
  name: "_Payment Rule"
  validationType: "L"
  values {
    uuid: "a456980a-fb40-11e8-a479-7a0060f0aa01"
    value: "K"
    name: "Tarjeta De Cr\303\251dito"
  }
  values {
    uuid: "a4569878-fb40-11e8-a479-7a0060f0aa01"
    value: "P"
    name: "A Cr\303\251dito"
  }
  values {
    uuid: "a45698dc-fb40-11e8-a479-7a0060f0aa01"
    value: "S"
    name: "Cheque"
  }
  values {
    uuid: "a4569940-fb40-11e8-a479-7a0060f0aa01"
    value: "T"
    name: "Dep\303\263sito Directo"
  }
  values {
    uuid: "a4597c5a-fb40-11e8-a479-7a0060f0aa01"
    value: "B"
    name: "Efectivo"
  }
  values {
    uuid: "a4597cc8-fb40-11e8-a479-7a0060f0aa01"
    value: "D"
    name: "D\303\251bito Directo"
  }
  values {
    uuid: "a4597d2c-fb40-11e8-a479-7a0060f0aa01"
    value: "M"
    name: "Mezclado"
  }
  values {
    uuid: "2ea2273a-1f96-11e9-a15f-8f58dba465bb"
    value: "C"
    name: "Cheque Diferido"
    description: "Cheque Diferido"
  }
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d65f758-fb40-11e8-a479-7a0060f0aa01"
name: "Morosidad"
description: "Reglas de morosidad para facturas vencidas"
help: "La Morosidad indica las reglas y m\303\251todos de c\303\241lculo de morosidad para pagos vencidos"
columnName: "C_Dunning_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 19

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d63387e-fb40-11e8-a479-7a0060f0aa01"
name: "Copias del Documento"
description: "N\303\272mero de copias a ser impresas"
help: "Copias de documento indica el n\303\272mero de copias de cada documento que ser\303\241 generado"
columnName: "DocumentCopies"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 11

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d6668dc-fb40-11e8-a479-7a0060f0aa01"
name: "Referencia de Orden de Socio del Negocio"
description: "N\303\272mero de referencia de de la transacci\303\263n (Orden de Venta; Orden de Compra) de su Socio del Negocio)"
help: "La referencia de orden del Socio del Negocio es la referencia para esta transacci\303\263n espec\303\255fica. Frecuentemente los n\303\272meros de orden de compras se dan para ser impresas en las facturas como una referencia m\303\241s f\303\241cil. Un n\303\272mero est\303\241ndar puede ser definido en la ventana Socio del Negocio (Cliente)."
columnName: "POReference"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d64ae5c-fb40-11e8-a479-7a0060f0aa01"
name: "Nombre 2"
description: "Nombre adicional"
columnName: "Name2"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
isSelectionColumn: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d641622-fb40-11e8-a479-7a0060f0aa01"
name: "Saludo"
description: "Saludo para imprimir en la correspondencia"
help: "Los saludos identifican los saludos a imprimir en la correspondencia"
columnName: "C_Greeting_ID"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 19

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d630840-fb40-11e8-a479-7a0060f0aa01"
name: "Imprimir Descuento"
description: "Imprimir el descuento en la Factura y la orden"
help: "El cuadro de verificaci\303\263n descuento Impreso indica si el descuento ser\303\241 impreso en el documento."
columnName: "IsDiscountPrinted"
isDisplayedGrid: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d5b14a0-fb40-11e8-a479-7a0060f0aa01"
name: "Compa\303\261\303\255a"
description: "Compa\303\261\303\255a para esta instalaci\303\263n"
help: "Compa\303\261\303\255a o entidad legal. No se pueden compartir datos entre diferentes compa\303\261\303\255as."
sequence: 10
columnName: "AD_Client_ID"
isDisplayed: true
isDisplayedGrid: true
isReadOnly: true
isMandatory: true
isAllowLogging: true
displayType: 19
defaultValue: "@#AD_Client_ID@"

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d3b391e-fb40-11e8-a479-7a0060f0aa01"
name: "Organizaci\303\263n"
description: "Entidad organizacional dentro de la compa\303\261\303\255a"
help: "Una organizaci\303\263n es una unidad de la compa\303\261\303\255a o entidad legal - Ej. Tiendas y departamentos. Es posible compartir datos entre organizaciones."
sequence: 20
columnName: "AD_Org_ID"
isDisplayed: true
isDisplayedGrid: true
isReadOnly: true
isSameLine: true
isMandatory: true
isAllowLogging: true
displayType: 19
defaultValue: "@#AD_Org_ID@"

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d39a270-fb40-11e8-a479-7a0060f0aa01"
name: "C\303\263digo"
description: "C\303\263digo para el registro en el formato requerido; debe ser \303\272nico"
help: "Un c\303\263digo le permite a usted un m\303\251todo r\303\241pido de encontrar un registro en particular"
sequence: 30
columnName: "Value"
isDisplayed: true
isDisplayedGrid: true
isReadOnly: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
isSelectionColumn: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8cf2ae7e-fb40-11e8-a479-7a0060f0aa01"
name: "Nombre"
description: "Identificador alfanum\303\251rico de la entidad."
help: "El nombre de una entidad (registro) se usa como una opci\303\263n de b\303\272squeda predeterminada adicional al c\303\263digo. El nombre es de hasta 60 caracteres de longitud."
sequence: 40
columnName: "Name"
isDisplayed: true
isDisplayedGrid: true
isReadOnly: true
isAllowCopy: true
isMandatory: true
isUpdateable: true
isIdentifier: true
isAllowLogging: true
isSelectionColumn: true
displayType: 10

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d612840-fb40-11e8-a479-7a0060f0aa01"
name: "Activo"
description: "El registro est\303\241 activo en el sistema"
help: "Hay dos m\303\251todos para que los registros no est\303\251n disponibles en el sistema: Uno es eliminar el registro; el otro es desactivarlo. Un registro desactivado no est\303\241 disponible para selecci\303\263n; pero est\303\241 disponible para Informes"
sequence: 50
columnName: "IsActive"
isDisplayed: true
isDisplayedGrid: true
isReadOnly: true
isSameLine: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20
defaultValue: "Y"

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8cef46bc-fb40-11e8-a479-7a0060f0aa01"
name: "Proveedor"
description: "Indica si el Socio del Negocio es un proveedor."
help: "El cuadro de verificaci\303\263n proveedor indica si este Socio del Negocio es un proveedor. Si se selecciona; campos adicionales ser\303\241n desplegados para identificar a este proveedor."
sequence: 60
columnName: "IsVendor"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d0773ea-fb40-11e8-a479-7a0060f0aa01"
name: "Agente Comercial"
description: "Indica si el empleado es un Agente de ventas"
help: "El cuadro de verificaci\303\263n Agente Comercial indica si este empleado es tambi\303\251n un Agente de la empresa."
sequence: 70
columnName: "IsSalesRep"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isSameLine: true
isMandatory: true
isUpdateable: true
isAllowLogging: true
displayType: 20

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8cecee3a-fb40-11e8-a479-7a0060f0aa01"
name: "Regla de Pago"
description: "Opci\303\263n de pago por compras"
help: "La Regla de Pago indica el m\303\251todo de pago de las compras"
sequence: 80
columnName: "PaymentRulePO"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 17
reference {
  uuid: "a47e0606-fb40-11e8-a479-7a0060f0aa01"
  name: "_Payment Rule"
  validationType: "L"
  values {
    uuid: "a456980a-fb40-11e8-a479-7a0060f0aa01"
    value: "K"
    name: "Tarjeta De Cr\303\251dito"
  }
  values {
    uuid: "a4569878-fb40-11e8-a479-7a0060f0aa01"
    value: "P"
    name: "A Cr\303\251dito"
  }
  values {
    uuid: "a45698dc-fb40-11e8-a479-7a0060f0aa01"
    value: "S"
    name: "Cheque"
  }
  values {
    uuid: "a4569940-fb40-11e8-a479-7a0060f0aa01"
    value: "T"
    name: "Dep\303\263sito Directo"
  }
  values {
    uuid: "a4597c5a-fb40-11e8-a479-7a0060f0aa01"
    value: "B"
    name: "Efectivo"
  }
  values {
    uuid: "a4597cc8-fb40-11e8-a479-7a0060f0aa01"
    value: "D"
    name: "D\303\251bito Directo"
  }
  values {
    uuid: "a4597d2c-fb40-11e8-a479-7a0060f0aa01"
    value: "M"
    name: "Mezclado"
  }
  values {
    uuid: "2ea2273a-1f96-11e9-a15f-8f58dba465bb"
    value: "C"
    name: "Cheque Diferido"
    description: "Cheque Diferido"
  }
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d034cde-fb40-11e8-a479-7a0060f0aa01"
name: "T\303\251rmino Pago OC"
description: "T\303\251rmino de Pago en una Orden de Compra"
help: "Las Condiciones de Pago de la OC indica los t\303\251rminos de pago que ser\303\241n usados cuando se llegue a facturar esta orden de compra"
sequence: 90
columnName: "PO_PaymentTerm_ID"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isSameLine: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 18
reference {
  uuid: "a47ddbd6-fb40-11e8-a479-7a0060f0aa01"
  name: "C_PaymentTerm"
  validationType: "T"
  referenceTable {
    uuid: "2ea694c8-1f96-11e9-a7e9-83827d68753a"
    tableName: "C_PaymentTerm"
    keyColumnName: "C_PaymentTerm_ID"
    displayColumnName: "Name"
  }
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8ceb0a52-fb40-11e8-a479-7a0060f0aa01"
name: "Lista de Precios de Compra"
description: "Lista de precios usada por este Socio del Negocio"
help: "Identifica la lista de precios usada por un proveedor para productos comprados por esta organizaci\303\263n."
sequence: 100
columnName: "PO_PriceList_ID"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 18
reference {
  uuid: "a47de748-fb40-11e8-a479-7a0060f0aa01"
  name: "M_PriceList"
  validationType: "T"
  referenceTable {
    uuid: "2ea669bc-1f96-11e9-a7c7-c3e1567080e1"
    tableName: "M_PriceList"
    keyColumnName: "M_PriceList_ID"
    displayColumnName: "Name"
  }
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8cf417dc-fb40-11e8-a479-7a0060f0aa01"
name: "Esquema Del Descuento en OC"
description: "Esquema para calcular el porcentaje de descuento comercial en compra"
sequence: 110
columnName: "PO_DiscountSchema_ID"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isSameLine: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 18
reference {
  uuid: "a47daee0-fb40-11e8-a479-7a0060f0aa01"
  name: "M_DiscountSchema not PL"
  validationType: "T"
  referenceTable {
    uuid: "2ea6bcd2-1f96-11e9-a837-7b20558cfdf4"
    tableName: "M_DiscountSchema"
    keyColumnName: "M_DiscountSchema_ID"
    displayColumnName: "Name"
    whereClause: "M_DiscountSchema.DiscountType<>\'P\'"
  }
}

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8d1f0604-fb40-11e8-a479-7a0060f0aa01"
name: "Manufacturer"
description: "Indicate role of this Business partner as Manufacturer"
sequence: 120
columnName: "IsManufacturer"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isUpdateable: true
isAllowLogging: true
displayType: 20
defaultValue: "\'N\'"

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8db10658-fb40-11e8-a479-7a0060f0aa01"
name: "AP Trial balance"
description: "Trial Balance for a period or date range"
help: "Select a Period (current period if empty) or enter a Account Date Range. If an account is selected, the balance is calculated based on the account type and the primary calendar of the client (i.e. for revenue/expense accounts from the beginning of the year). If no account is selected, the balance is the sum of all transactions before the selected account range or first day of the period selected. You can select an alternative Reporting Hierarchy."
sequence: 160
columnName: "APEnquiry"
isDisplayed: true
isAllowCopy: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 28
process {
  uuid: "a42cfb4e-fb40-11e8-a479-7a0060f0aa01"
  name: "AP Trial balance"
  description: "Trial Balance for a period or date range"
  help: "Select a Period (current period if empty) or enter a Account Date Range. If an account is selected, the balance is calculated based on the account type and the primary calendar of the client (i.e. for revenue/expense accounts from the beginning of the year). If no account is selected, the balance is the sum of all transactions before the selected account range or first day of the period selected. You can select an alternative Reporting Hierarchy."
  isReport: true
}
identifierSequence: 160

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8db0f564-fb40-11e8-a479-7a0060f0aa01"
name: "Unapplied AR Payments"
description: "Payment Detail Report"
help: "Type adjusted payments (receipts positive, payments negative) with allocated and available amounts"
sequence: 170
columnName: "UnappliedPayments"
isDisplayed: true
isAllowCopy: true
isSameLine: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 28
process {
  uuid: "a42cfbb2-fb40-11e8-a479-7a0060f0aa01"
  name: "Unapplied AR Payments"
  description: "Payment Detail Report"
  help: "Type adjusted payments (receipts positive, payments negative) with allocated and available amounts"
  isReport: true
}
identifierSequence: 170

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8db10de2-fb40-11e8-a479-7a0060f0aa01"
name: "Open Invoices"
description: "Open Item (Invoice) List"
help: "Displays all unpaid invoices for a given Business Partner and date range. Please note that Invoices paid in Cash will appear in Open Items until the Cash Journal is processed."
sequence: 180
columnName: "VendorOpenInvoices"
isDisplayed: true
isAllowCopy: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 28
process {
  uuid: "a42cf996-fb40-11e8-a479-7a0060f0aa01"
  name: "Open Invoices"
  description: "Open Item (Invoice) List"
  help: "Displays all unpaid invoices for a given Business Partner and date range. Please note that Invoices paid in Cash will appear in Open Items until the Cash Journal is processed."
  isReport: true
}
identifierSequence: 180

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Field: uuid: "8db10130-fb40-11e8-a479-7a0060f0aa01"
name: "Not Posted Invoice"
sequence: 190
columnName: "NotPosted"
isDisplayed: true
isAllowCopy: true
isSameLine: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 28
process {
  uuid: "a42cff4a-fb40-11e8-a479-7a0060f0aa01"
  name: "Not Posted Invoice"
  isReport: true
}
identifierSequence: 190

ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestTab
INFORMACIÓN: Tab: uuid: "a49fb4e0-fb40-11e8-a479-7a0060f0aa01"
name: "Proveedor"
description: "Definir Par\303\241metros del Proveedor"
help: "La pesta\303\261a Proveedor define un tercero que es un proveedor de esta organizaci\303\263n. Si el cuadro de verificaci\303\263n Proveedor esta seleccionado, los campos relacionados a proveedores son desplegados."
tableName: "C_BPartner"
sequence: 40
tabLevel: 1
isSingleRow: true
isDeleteable: true
linkColumnName: "C_BPartner_ID"
fields {
  uuid: "8d635656-fb40-11e8-a479-7a0060f0aa01"
  name: "Grupo de Socio del Negocio"
  description: "ID del Grupo de Socio del Negocio"
  help: "La ID Grupo del Socio del Negocio proporciona un m\303\251todo de definir valores predeterminados a ser usados para Socios del Negocio individuales."
  columnName: "C_BP_Group_ID"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 19
}
fields {
  uuid: "8d60ee02-fb40-11e8-a479-7a0060f0aa01"
  name: "Descripci\303\263n de Orden"
  description: "Descripci\303\263n a ser usada en \303\263rdenes"
  help: "La descripci\303\263n de la orden identifica la descripci\303\263n est\303\241ndar a usar en \303\263rdenes para este cliente"
  columnName: "SO_Description"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d36f9f8-fb40-11e8-a479-7a0060f0aa01"
  name: "Regla de Facturaci\303\263n"
  description: "Frecuencia y m\303\251todos de facturaci\303\263n"
  help: "La regla de facturaci\303\263n define c\303\263mo se le factura a un Socio del Negocio y la frecuencia de facturaci\303\263n."
  columnName: "InvoiceRule"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 17
  reference {
    uuid: "a47db958-fb40-11e8-a479-7a0060f0aa01"
    name: "C_Order InvoiceRule"
    validationType: "L"
    values {
      uuid: "a4553320-fb40-11e8-a479-7a0060f0aa01"
      value: "D"
      name: "Despu\303\251s de Entrega"
      description: "Factura por entrega"
    }
    values {
      uuid: "a45533a2-fb40-11e8-a479-7a0060f0aa01"
      value: "S"
      name: "Programa del cliente despu\303\251s de entrega"
      description: "Factura seg\303\272n el programa de Facturaci\303\263n del Cliente"
    }
    values {
      uuid: "a45540a4-fb40-11e8-a479-7a0060f0aa01"
      value: "I"
      name: "Inmediato"
      description: "Facturaci\303\263n Inmediata"
    }
  }
}
fields {
  uuid: "8d5f1898-fb40-11e8-a479-7a0060f0aa01"
  name: "Regla de Entrega"
  description: "Define los tiempos de entrega"
  help: "La Regla de Entrega indica cuando una orden debe ser entregada. Por Ej. Si la orden debiera entregarse cuando est\303\241 completa; cuando una partida est\303\241 completa o cuando el producto llega a estar disponible."
  columnName: "DeliveryRule"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 17
  reference {
    uuid: "a47e7848-fb40-11e8-a479-7a0060f0aa01"
    name: "C_Order DeliveryRule"
    validationType: "L"
    values {
      uuid: "a4553410-fb40-11e8-a479-7a0060f0aa01"
      value: "R"
      name: "Despu\303\251s del cobro"
      description: "Despu\303\251s del cobro"
    }
    values {
      uuid: "a4553eb0-fb40-11e8-a479-7a0060f0aa01"
      value: "A"
      name: "Disponibilidad"
      description: "Tan pronto como el art\303\255culo est\303\251 disponible"
    }
    values {
      uuid: "a4553fa0-fb40-11e8-a479-7a0060f0aa01"
      value: "L"
      name: "L\303\255nea Completa"
      description: "Tan pronto como todos los art\303\255culos de una l\303\255nea est\303\251n disponibles"
    }
    values {
      uuid: "a455402c-fb40-11e8-a479-7a0060f0aa01"
      value: "O"
      name: "Orden Completa"
      description: "Tan pronto como todos los art\303\255culos de una orden est\303\251n disponibles"
    }
    values {
      uuid: "a4587076-fb40-11e8-a479-7a0060f0aa01"
      value: "F"
      name: "Forzado"
    }
    values {
      uuid: "a459d36c-fb40-11e8-a479-7a0060f0aa01"
      value: "M"
      name: "Manual"
    }
  }
}
fields {
  uuid: "8d641564-fb40-11e8-a479-7a0060f0aa01"
  name: "Agente Comercial"
  description: "Agente Comercial"
  help: "El Agente comercial indica el Agente comercial para esta regi\303\263n."
  columnName: "SalesRep_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 18
  reference {
    uuid: "a47e14c0-fb40-11e8-a479-7a0060f0aa01"
    name: "AD_User - SalesRep"
    validationType: "T"
    referenceTable {
      uuid: "2ea6d9b0-1f96-11e9-a86f-07cba138b6eb"
      tableName: "AD_User"
      keyColumnName: "AD_User_ID"
      displayColumnName: "Name"
      whereClause: "EXISTS (SELECT * FROM C_BPartner bp WHERE AD_User.C_BPartner_ID=bp.C_BPartner_ID AND bp.IsSalesRep=\'Y\')\n"
    }
  }
}
fields {
  uuid: "8d6135a6-fb40-11e8-a479-7a0060f0aa01"
  name: "Regla de Costo de Flete"
  description: "M\303\251todo para cargar el flete"
  help: "La regla de costo de flete indica el m\303\251todo usado para cargar los fletes."
  columnName: "FreightCostRule"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 17
  reference {
    uuid: "a47dba7a-fb40-11e8-a479-7a0060f0aa01"
    name: "C_Order FreightCostRule"
    validationType: "L"
    values {
      uuid: "a4554112-fb40-11e8-a479-7a0060f0aa01"
      value: "I"
      name: "Flete Incluido"
      description: "Costo de flete incluido"
    }
    values {
      uuid: "a455572e-fb40-11e8-a479-7a0060f0aa01"
      value: "C"
      name: "Calculado"
      description: "C\303\241lculo basado en la regla de flete del producto"
    }
    values {
      uuid: "a45557a6-fb40-11e8-a479-7a0060f0aa01"
      value: "F"
      name: "Precio Fijo"
      description: "Precio fijo de flete"
    }
    values {
      uuid: "a460c5dc-fb40-11e8-a479-7a0060f0aa01"
      value: "L"
      name: "L\303\255nea"
      description: "Introducido al nivel de l\303\255nea"
    }
  }
}
fields {
  uuid: "8d5fad4e-fb40-11e8-a479-7a0060f0aa01"
  name: "V\303\255a de Entrega"
  description: "Como ser\303\241 entregada la orden"
  help: "La v\303\255a de entrega indica como el producto deber\303\255a ser entregado. Por Ej. Si la orden ser\303\241 recogida o embarcada."
  columnName: "DeliveryViaRule"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 17
  reference {
    uuid: "a47dbb42-fb40-11e8-a479-7a0060f0aa01"
    name: "C_Order DeliveryViaRule"
    validationType: "L"
    values {
      uuid: "a4556502-fb40-11e8-a479-7a0060f0aa01"
      value: "D"
      name: "Entrega"
    }
    values {
      uuid: "a455657a-fb40-11e8-a479-7a0060f0aa01"
      value: "S"
      name: "Transportista"
    }
    values {
      uuid: "a459d3d0-fb40-11e8-a479-7a0060f0aa01"
      value: "P"
      name: "Recolecci\303\263n"
    }
  }
}
fields {
  uuid: "8d572d7c-fb40-11e8-a479-7a0060f0aa01"
  name: "Socio del Negocio"
  description: "Identifica un Socio del Negocio"
  help: "Un Socio del Negocio es cualquiera con quien usted realiza transacciones. Este puede incluir Proveedores; Clientes; Empleados o Vendedores."
  columnName: "C_BPartner_ID"
  isDisplayedGrid: true
  isMandatory: true
  isKey: true
  isAllowLogging: true
  displayType: 13
}
fields {
  uuid: "8d63cbe0-fb40-11e8-a479-7a0060f0aa01"
  name: "Esq List Precios/Desc"
  description: "Esquema para calcular el porcentaje de descuento comercial"
  help: "Despu\303\251s del c\303\241lculo de precio (est\303\241ndar); el porcentaje de descuento comercial es calculado y aplicado resultando en el precio final"
  columnName: "M_DiscountSchema_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 18
  reference {
    uuid: "a47daee0-fb40-11e8-a479-7a0060f0aa01"
    name: "M_DiscountSchema not PL"
    validationType: "T"
    referenceTable {
      uuid: "2ea6bcd2-1f96-11e9-a837-7b20558cfdf4"
      tableName: "M_DiscountSchema"
      keyColumnName: "M_DiscountSchema_ID"
      displayColumnName: "Name"
      whereClause: "M_DiscountSchema.DiscountType<>\'P\'"
    }
  }
}
fields {
  uuid: "8d60aa50-fb40-11e8-a479-7a0060f0aa01"
  name: "Env\303\255a Email"
  description: "Permite enviar el documento Email"
  help: "Env\303\255e los email con el documento unido (ej. factura, nota de entrega, etc.)"
  columnName: "SendEMail"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d60b55e-fb40-11e8-a479-7a0060f0aa01"
  name: "Socio del Negocio Padre"
  description: "Socio del Negocio Padre"
  help: "El padre (organizaci\303\263n) del Socio del Negocio para reportar prop\303\263sitos."
  columnName: "BPartner_Parent_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 18
  reference {
    uuid: "a47d092c-fb40-11e8-a479-7a0060f0aa01"
    name: "C_BPartner Parent"
    validationType: "T"
    referenceTable {
      uuid: "2ea664f8-1f96-11e9-a7bd-477f3868a0f6"
      tableName: "C_BPartner"
      keyColumnName: "C_BPartner_ID"
      displayColumnName: "Name"
      whereClause: "C_BPartner.IsSummary=\'Y\'"
    }
  }
}
fields {
  uuid: "8d60fb7c-fb40-11e8-a479-7a0060f0aa01"
  name: "Formato Impresi\303\263n Factura"
  description: "Formato de impresi\303\263n usado para imprimir facturas"
  help: "Es necesario definir un formato para imprimir el documento"
  columnName: "Invoice_PrintFormat_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 18
  reference {
    uuid: "a47dfa08-fb40-11e8-a479-7a0060f0aa01"
    name: "AD_PrintFormat Invoice"
    validationType: "T"
    referenceTable {
      uuid: "2ea65f26-1f96-11e9-a7b0-7b837c354c05"
      tableName: "AD_PrintFormat"
      keyColumnName: "AD_PrintFormat_ID"
      displayColumnName: "Name"
      whereClause: "AD_PrintFormat.AD_Table_ID=516"
    }
  }
}
fields {
  uuid: "8d6750a8-fb40-11e8-a479-7a0060f0aa01"
  name: "Estado del Cr\303\251dito"
  description: "Estado del cr\303\251dito de ventas"
  help: "Solamente para la documentaci\303\263n."
  columnName: "SOCreditStatus"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 17
  reference {
    uuid: "a47d457c-fb40-11e8-a479-7a0060f0aa01"
    name: "C_BPartner SOCreditStatus"
    validationType: "L"
    values {
      uuid: "a45858d4-fb40-11e8-a479-7a0060f0aa01"
      value: "S"
      name: "Cr\303\251dito Detenido"
    }
    values {
      uuid: "a4585a0a-fb40-11e8-a479-7a0060f0aa01"
      value: "H"
      name: "Cr\303\251dito Retenido"
    }
    values {
      uuid: "a4585adc-fb40-11e8-a479-7a0060f0aa01"
      value: "W"
      name: "Cr\303\251dito en Verificaci\303\263n"
    }
    values {
      uuid: "a4585b90-fb40-11e8-a479-7a0060f0aa01"
      value: "X"
      name: "Sin Verificaci\303\263n de Cr\303\251dito"
    }
    values {
      uuid: "a4585c12-fb40-11e8-a479-7a0060f0aa01"
      value: "O"
      name: "Cr\303\251dito Correcto"
    }
  }
}
fields {
  uuid: "8d64a2ea-fb40-11e8-a479-7a0060f0aa01"
  name: "M\303\255n de Vida \303\272til %"
  description: "M\303\255nimo de vida \303\272til en porcentaje basados en la fecha lo que garantiza el producto."
  help: "M\303\255nimo de vida \303\272til en productos con fecha de garant\303\255a. Si > 0 Usted no puede seleccionar productos con una vida \303\272til. (fecha - dia de la garant\303\255a) / menos que la vida \303\272til del m\303\255nimo, a menos que usted seleccione \"toda demostraci\303\263n\""
  columnName: "ShelfLifeMinPct"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 11
}
fields {
  uuid: "8d66d2fe-fb40-11e8-a479-7a0060f0aa01"
  name: "Liga Organizaci\303\263n"
  description: "Integraci\303\263n de Socio del Negocio a una Organizaci\303\263n"
  help: "Si el Socio del Negocio esta en otra organizaci\303\263n, seleccione la organizaci\303\263n o fije para crear una nueva organizaci\303\263n. Usted liga a Socio del Negocio a una organizaci\303\263n para crear los documentos expl\303\255citos para la Integraci\303\263n-Org transacci\303\263n. Si usted crea una nueva organizaci\303\263n, usted puede proveer un tipo de la organizaci\303\263n. Si usted selecciona un rol, el acceso a la nueva organizaci\303\263n se limita a ese rol, si no todo los roles (no manual) del cliente tendr\303\241n acceso a la nueva organizaci\303\263n."
  columnName: "AD_OrgBP_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 28
  readOnlyLogic: "@AD_OrgBP_ID@!0"
  process {
    uuid: "a42b86e2-fb40-11e8-a479-7a0060f0aa01"
    name: "Liga Organizaci\303\263n"
    description: "Integraci\303\263n de Socio del Negocio a una Organizaci\303\263n"
    help: "Si el Socio del Negocio esta en otra organizaci\303\263n, seleccione la organizaci\303\263n o fije para crear una nueva organizaci\303\263n. Usted liga a Socio del Negocio a una organizaci\303\263n para crear los documentos expl\303\255citos para la Integraci\303\263n-Org transacci\303\263n. Si usted crea una nueva organizaci\303\263n, usted puede proveer un tipo de la organizaci\303\263n. Si usted selecciona un rol, el acceso a la nueva organizaci\303\263n se limita a ese rol, si no todo los roles (no manual) del cliente tendr\303\241n acceso a la nueva organizaci\303\263n."
  }
}
fields {
  uuid: "8d36d306-fb40-11e8-a479-7a0060f0aa01"
  name: "% Descuento"
  description: "Porcentaje de descuento simple"
  columnName: "FlatDiscount"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 22
}
fields {
  uuid: "8d62dc3a-fb40-11e8-a479-7a0060f0aa01"
  name: "Saldo Actual"
  description: "Total de importe en balance abierto, en las cuentas primarias actuales."
  help: "La cantidad abierta total del balance es la cantidad abierta calculada del art\303\255culo para la actividad del cliente y del proveedor. Si el equilibrio est\303\241 debajo de cero, debemos al Socio del Negocio. El importe se utiliza para la gerencia de cr\303\251dito. Las facturas y las asignaciones del pago determinan el equilibrio abierto (es decir no las \303\263rdenes o los pagos).\n\nThe Total Open Balance Amount is the calculated open item amount for Customer and Vendor activity.  If the Balance is below zero, we owe the Business Partner.  The amout is used for Credit Management.\n\nInvoices and Payment Allocations determine the Open Balance (i.e. not Orders or Payments)."
  columnName: "TotalOpenBalance"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 12
}
fields {
  uuid: "8d64b2b2-fb40-11e8-a479-7a0060f0aa01"
  name: "Descripci\303\263n"
  description: "Descripci\303\263n corta opcional del registro"
  help: "Una descripci\303\263n esta limitada a 255 caracteres"
  columnName: "Description"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  isSelectionColumn: true
  displayType: 10
}
fields {
  uuid: "8d6006e0-fb40-11e8-a479-7a0060f0aa01"
  name: "Volumen de Ventas"
  description: "Volumen total de Ventas"
  help: "El Volumen de ventas indica el volumen total de ventas para un Socio del Negocio"
  columnName: "SalesVolume"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 11
}
fields {
  uuid: "8d64a3a8-fb40-11e8-a479-7a0060f0aa01"
  name: "No. de Referencia"
  description: "Su n\303\272mero de cliente o proveedor con el Socio del Negocio."
  help: "El n\303\272mero de referencia puede ser impreso en \303\263rdenes y facturas para permitirle a su Socio del Negocio identificar m\303\241s r\303\241pido sus registros."
  columnName: "ReferenceNo"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d60fc12-fb40-11e8-a479-7a0060f0aa01"
  name: "DUNS"
  description: "DUNS (del ingl\303\251s Data Universal Numbering System o Sistema Universal de Numeraci\303\263n de Datos)"
  help: "Usado por EDI - para detalles ver www.dnb.com/dunsno/list.htm"
  columnName: "DUNS"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d64bf50-fb40-11e8-a479-7a0060f0aa01"
  name: "Empleados"
  description: "N\303\272mero de empleados"
  help: "Indica el n\303\272mero de empleados de este Socio del Negocio. Este campo se despliega solamente para prospectos."
  columnName: "NumberEmployees"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 11
}
fields {
  uuid: "8d5ed874-fb40-11e8-a479-7a0060f0aa01"
  name: "N\303\272mero Identificaci\303\263n"
  description: "N\303\272mero de Identificaci\303\263n Tributaria"
  help: "N\303\272mero de Identificaci\303\263n Tributaria es el n\303\272mero de identificaci\303\263n gubernamental de esta entidad"
  columnName: "TaxID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d62a27e-fb40-11e8-a479-7a0060f0aa01"
  name: "NAICS/SIC"
  description: "Codigo est\303\241ndard de la industria o sucesor NAIC - http://www.osha.gov/oshstats/sicser.html"
  help: "El NAICS/SIC identifica cualquiera de esos c\303\263digos que puedan ser aplicables a este Socio del Negocio"
  columnName: "NAICS"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d63d07c-fb40-11e8-a479-7a0060f0aa01"
  name: "Entidad Acumulada"
  description: "Esta es una entidad sumaria"
  help: "Una entidad sumaria representa una rama en un \303\241rbol mas bien que un nodo final. Las entidades sumarias son usadas para reportar y no tienen valores propios"
  columnName: "IsSummary"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d662b7e-fb40-11e8-a479-7a0060f0aa01"
  name: "Lenguaje"
  description: "Lenguaje para esta entidad"
  help: "El lenguaje identifica el lenguaje a usar para el despliegue"
  columnName: "AD_Language"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 18
  reference {
    uuid: "a47dd348-fb40-11e8-a479-7a0060f0aa01"
    name: "AD_Language System"
    validationType: "T"
    referenceTable {
      uuid: "2ea635be-1f96-11e9-a7ab-fb7e255c2700"
      tableName: "AD_Language"
      keyColumnName: "AD_Language"
      displayColumnName: "Name"
      whereClause: "(AD_Language.IsSystemLanguage=\'Y\' OR AD_Language.IsBaseLanguage=\'Y\')"
    }
  }
}
fields {
  uuid: "8d5fb2c6-fb40-11e8-a479-7a0060f0aa01"
  name: "Cliente"
  description: "Indica si el Socio del Negocio es un cliente"
  help: "El cuadro de verificaci\303\263n cliente indica si el Socio del Negocio es un cliente. Si se seleccionan campos adicionales desplegar\303\241n informaci\303\263n adicional para definir al cliente."
  columnName: "IsCustomer"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d61351a-fb40-11e8-a479-7a0060f0aa01"
  name: "Programa de Facturaci\303\263n"
  description: "Programa para generar facturas"
  help: "El programa de facturaci\303\263n identifica la frecuencia usada cuando se generan facturas."
  columnName: "C_InvoiceSchedule_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 19
}
fields {
  uuid: "8d630796-fb40-11e8-a479-7a0060f0aa01"
  name: "Prospecto Activo"
  description: "Indica un prospecto en oposici\303\263n a un cliente activo."
  help: "El cuadro de verificaci\303\263n prospecto indica una entidad que es un prospecto activo pero no es a\303\272n un cliente."
  columnName: "IsProspect"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d6306e2-fb40-11e8-a479-7a0060f0aa01"
  name: "Primera Venta"
  description: "Fecha de la primera venta"
  help: "La fecha de la Primera Venta indica la fecha de la primera venta a este Socio del Negocio"
  columnName: "FirstSale"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 15
}
fields {
  uuid: "8d6749f0-fb40-11e8-a479-7a0060f0aa01"
  name: "L\303\255mite de Cr\303\251dito"
  description: "Total pendiente del total de la factura pendiente."
  help: "El l\303\255mite de cr\303\251dito indica el total de deuda permitida."
  columnName: "SO_CreditLimit"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 12
}
fields {
  uuid: "8d627556-fb40-11e8-a479-7a0060f0aa01"
  name: "Cr\303\251dito Usado"
  description: "Balance actual abierto"
  help: "El cr\303\251dito usado indica la cantidad total de facturas abiertas o sin pagar del Socio del Negocio"
  columnName: "SO_CreditUsed"
  isDisplayedGrid: true
  isMandatory: true
  isAllowLogging: true
  displayType: 12
}
fields {
  uuid: "8d5a33dc-fb40-11e8-a479-7a0060f0aa01"
  name: "Costo de Adquisici\303\263n"
  description: "Costo de ganar el prospecto como cliente"
  help: "El costo de adquisici\303\263n identifica el costo asociado con hacer de este prospecto un cliente"
  columnName: "AcqusitionCost"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 37
}
fields {
  uuid: "8d62c376-fb40-11e8-a479-7a0060f0aa01"
  name: "Valor Esperado"
  description: "Total de ingresos esperados"
  help: "El valor en el tiempo de vida potencial es el ingreso anticipado a ser generado por este Socio del Negocio."
  columnName: "PotentialLifeTimeValue"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 12
}
fields {
  uuid: "8d635f5c-fb40-11e8-a479-7a0060f0aa01"
  name: "T\303\251rmino de Pago"
  description: "Condiciones de pago de esta transacci\303\263n"
  help: "Las condiciones de pago indican el m\303\251todo y tiempo de pago para esta transacci\303\263n."
  columnName: "C_PaymentTerm_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 19
}
fields {
  uuid: "8d629568-fb40-11e8-a479-7a0060f0aa01"
  name: "Valor Total Transacciones"
  description: "Ingreso de tiempo de vida Actual"
  help: "El valor de tiempo de vida actual es el ingreso registrado y generado por este Socio del Negocio."
  columnName: "ActualLifeTimeValue"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 12
}
fields {
  uuid: "8d629658-fb40-11e8-a479-7a0060f0aa01"
  name: "Participaci\303\263n"
  description: "Participaci\303\263n del cliente."
  help: "La participaci\303\263n indica el porcentaje de este Socio del Negocio."
  columnName: "ShareOfCustomer"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 11
}
fields {
  uuid: "8d62b9e4-fb40-11e8-a479-7a0060f0aa01"
  name: "Empleado"
  description: "Indica si el Socio del Negocio es un empleado"
  help: "El cuadro de verificaci\303\263n empleado indica si este Socio del Negocio es un empleado. Si se selecciona se desplegar\303\241n campos adicionales para identificar a este empleado."
  columnName: "IsEmployee"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d626462-fb40-11e8-a479-7a0060f0aa01"
  name: "Lista de Precios"
  description: "Identificador \303\272nico de mi lista de precios"
  help: "Listas de precios son usadas para determinar el precio; margen y costo de art\303\255culos comprados o vendidos."
  columnName: "M_PriceList_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 19
}
fields {
  uuid: "8d5a35a8-fb40-11e8-a479-7a0060f0aa01"
  name: "Transacci\303\263n de una vez"
  columnName: "IsOneTime"
  isDisplayedGrid: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d5a3684-fb40-11e8-a479-7a0060f0aa01"
  name: "Direcci\303\263n Web"
  description: "Direcci\303\263n Web complete - e.g. http://www.globalqss.com"
  help: "El URL define una direcci\303\263n web para este Socio del Negocio"
  columnName: "URL"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 40
}
fields {
  uuid: "8d60bf86-fb40-11e8-a479-7a0060f0aa01"
  name: "Exento de Impuesto en Venta"
  description: "Este Socio del Negocio es exento (o no le debo facturar) del impuesto de ventas."
  help: "El cuadro de verificaci\303\263n exento de impuesto identifica un Socio del Negocio quien no esta sujeto al impuesto de ventas."
  columnName: "IsTaxExempt"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d5a34c2-fb40-11e8-a479-7a0060f0aa01"
  name: "Valuaci\303\263n ABC"
  description: "Clasificaci\303\263n o importancia de un Socio del Negocio."
  help: "La valuaci\303\263n es usada para identificar la importancia del Socio del Negocio"
  columnName: "Rating"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d62c2c2-fb40-11e8-a479-7a0060f0aa01"
  name: "Regla de Pago"
  description: "Como se pagar\303\241 la factura"
  help: "La Regla de Pagos indica el m\303\251todo de pago de la factura"
  columnName: "PaymentRule"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 17
  reference {
    uuid: "a47e0606-fb40-11e8-a479-7a0060f0aa01"
    name: "_Payment Rule"
    validationType: "L"
    values {
      uuid: "a456980a-fb40-11e8-a479-7a0060f0aa01"
      value: "K"
      name: "Tarjeta De Cr\303\251dito"
    }
    values {
      uuid: "a4569878-fb40-11e8-a479-7a0060f0aa01"
      value: "P"
      name: "A Cr\303\251dito"
    }
    values {
      uuid: "a45698dc-fb40-11e8-a479-7a0060f0aa01"
      value: "S"
      name: "Cheque"
    }
    values {
      uuid: "a4569940-fb40-11e8-a479-7a0060f0aa01"
      value: "T"
      name: "Dep\303\263sito Directo"
    }
    values {
      uuid: "a4597c5a-fb40-11e8-a479-7a0060f0aa01"
      value: "B"
      name: "Efectivo"
    }
    values {
      uuid: "a4597cc8-fb40-11e8-a479-7a0060f0aa01"
      value: "D"
      name: "D\303\251bito Directo"
    }
    values {
      uuid: "a4597d2c-fb40-11e8-a479-7a0060f0aa01"
      value: "M"
      name: "Mezclado"
    }
    values {
      uuid: "2ea2273a-1f96-11e9-a15f-8f58dba465bb"
      value: "C"
      name: "Cheque Diferido"
      description: "Cheque Diferido"
    }
  }
}
fields {
  uuid: "8d65f758-fb40-11e8-a479-7a0060f0aa01"
  name: "Morosidad"
  description: "Reglas de morosidad para facturas vencidas"
  help: "La Morosidad indica las reglas y m\303\251todos de c\303\241lculo de morosidad para pagos vencidos"
  columnName: "C_Dunning_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 19
}
fields {
  uuid: "8d63387e-fb40-11e8-a479-7a0060f0aa01"
  name: "Copias del Documento"
  description: "N\303\272mero de copias a ser impresas"
  help: "Copias de documento indica el n\303\272mero de copias de cada documento que ser\303\241 generado"
  columnName: "DocumentCopies"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 11
}
fields {
  uuid: "8d6668dc-fb40-11e8-a479-7a0060f0aa01"
  name: "Referencia de Orden de Socio del Negocio"
  description: "N\303\272mero de referencia de de la transacci\303\263n (Orden de Venta; Orden de Compra) de su Socio del Negocio)"
  help: "La referencia de orden del Socio del Negocio es la referencia para esta transacci\303\263n espec\303\255fica. Frecuentemente los n\303\272meros de orden de compras se dan para ser impresas en las facturas como una referencia m\303\241s f\303\241cil. Un n\303\272mero est\303\241ndar puede ser definido en la ventana Socio del Negocio (Cliente)."
  columnName: "POReference"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 10
}
fields {
  uuid: "8d64ae5c-fb40-11e8-a479-7a0060f0aa01"
  name: "Nombre 2"
  description: "Nombre adicional"
  columnName: "Name2"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  isSelectionColumn: true
  displayType: 10
}
fields {
  uuid: "8d641622-fb40-11e8-a479-7a0060f0aa01"
  name: "Saludo"
  description: "Saludo para imprimir en la correspondencia"
  help: "Los saludos identifican los saludos a imprimir en la correspondencia"
  columnName: "C_Greeting_ID"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 19
}
fields {
  uuid: "8d630840-fb40-11e8-a479-7a0060f0aa01"
  name: "Imprimir Descuento"
  description: "Imprimir el descuento en la Factura y la orden"
  help: "El cuadro de verificaci\303\263n descuento Impreso indica si el descuento ser\303\241 impreso en el documento."
  columnName: "IsDiscountPrinted"
  isDisplayedGrid: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d5b14a0-fb40-11e8-a479-7a0060f0aa01"
  name: "Compa\303\261\303\255a"
  description: "Compa\303\261\303\255a para esta instalaci\303\263n"
  help: "Compa\303\261\303\255a o entidad legal. No se pueden compartir datos entre diferentes compa\303\261\303\255as."
  sequence: 10
  columnName: "AD_Client_ID"
  isDisplayed: true
  isDisplayedGrid: true
  isReadOnly: true
  isMandatory: true
  isAllowLogging: true
  displayType: 19
  defaultValue: "@#AD_Client_ID@"
}
fields {
  uuid: "8d3b391e-fb40-11e8-a479-7a0060f0aa01"
  name: "Organizaci\303\263n"
  description: "Entidad organizacional dentro de la compa\303\261\303\255a"
  help: "Una organizaci\303\263n es una unidad de la compa\303\261\303\255a o entidad legal - Ej. Tiendas y departamentos. Es posible compartir datos entre organizaciones."
  sequence: 20
  columnName: "AD_Org_ID"
  isDisplayed: true
  isDisplayedGrid: true
  isReadOnly: true
  isSameLine: true
  isMandatory: true
  isAllowLogging: true
  displayType: 19
  defaultValue: "@#AD_Org_ID@"
}
fields {
  uuid: "8d39a270-fb40-11e8-a479-7a0060f0aa01"
  name: "C\303\263digo"
  description: "C\303\263digo para el registro en el formato requerido; debe ser \303\272nico"
  help: "Un c\303\263digo le permite a usted un m\303\251todo r\303\241pido de encontrar un registro en particular"
  sequence: 30
  columnName: "Value"
  isDisplayed: true
  isDisplayedGrid: true
  isReadOnly: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  isSelectionColumn: true
  displayType: 10
}
fields {
  uuid: "8cf2ae7e-fb40-11e8-a479-7a0060f0aa01"
  name: "Nombre"
  description: "Identificador alfanum\303\251rico de la entidad."
  help: "El nombre de una entidad (registro) se usa como una opci\303\263n de b\303\272squeda predeterminada adicional al c\303\263digo. El nombre es de hasta 60 caracteres de longitud."
  sequence: 40
  columnName: "Name"
  isDisplayed: true
  isDisplayedGrid: true
  isReadOnly: true
  isAllowCopy: true
  isMandatory: true
  isUpdateable: true
  isIdentifier: true
  isAllowLogging: true
  isSelectionColumn: true
  displayType: 10
}
fields {
  uuid: "8d612840-fb40-11e8-a479-7a0060f0aa01"
  name: "Activo"
  description: "El registro est\303\241 activo en el sistema"
  help: "Hay dos m\303\251todos para que los registros no est\303\251n disponibles en el sistema: Uno es eliminar el registro; el otro es desactivarlo. Un registro desactivado no est\303\241 disponible para selecci\303\263n; pero est\303\241 disponible para Informes"
  sequence: 50
  columnName: "IsActive"
  isDisplayed: true
  isDisplayedGrid: true
  isReadOnly: true
  isSameLine: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
  defaultValue: "Y"
}
fields {
  uuid: "8cef46bc-fb40-11e8-a479-7a0060f0aa01"
  name: "Proveedor"
  description: "Indica si el Socio del Negocio es un proveedor."
  help: "El cuadro de verificaci\303\263n proveedor indica si este Socio del Negocio es un proveedor. Si se selecciona; campos adicionales ser\303\241n desplegados para identificar a este proveedor."
  sequence: 60
  columnName: "IsVendor"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8d0773ea-fb40-11e8-a479-7a0060f0aa01"
  name: "Agente Comercial"
  description: "Indica si el empleado es un Agente de ventas"
  help: "El cuadro de verificaci\303\263n Agente Comercial indica si este empleado es tambi\303\251n un Agente de la empresa."
  sequence: 70
  columnName: "IsSalesRep"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isSameLine: true
  isMandatory: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
}
fields {
  uuid: "8cecee3a-fb40-11e8-a479-7a0060f0aa01"
  name: "Regla de Pago"
  description: "Opci\303\263n de pago por compras"
  help: "La Regla de Pago indica el m\303\251todo de pago de las compras"
  sequence: 80
  columnName: "PaymentRulePO"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 17
  reference {
    uuid: "a47e0606-fb40-11e8-a479-7a0060f0aa01"
    name: "_Payment Rule"
    validationType: "L"
    values {
      uuid: "a456980a-fb40-11e8-a479-7a0060f0aa01"
      value: "K"
      name: "Tarjeta De Cr\303\251dito"
    }
    values {
      uuid: "a4569878-fb40-11e8-a479-7a0060f0aa01"
      value: "P"
      name: "A Cr\303\251dito"
    }
    values {
      uuid: "a45698dc-fb40-11e8-a479-7a0060f0aa01"
      value: "S"
      name: "Cheque"
    }
    values {
      uuid: "a4569940-fb40-11e8-a479-7a0060f0aa01"
      value: "T"
      name: "Dep\303\263sito Directo"
    }
    values {
      uuid: "a4597c5a-fb40-11e8-a479-7a0060f0aa01"
      value: "B"
      name: "Efectivo"
    }
    values {
      uuid: "a4597cc8-fb40-11e8-a479-7a0060f0aa01"
      value: "D"
      name: "D\303\251bito Directo"
    }
    values {
      uuid: "a4597d2c-fb40-11e8-a479-7a0060f0aa01"
      value: "M"
      name: "Mezclado"
    }
    values {
      uuid: "2ea2273a-1f96-11e9-a15f-8f58dba465bb"
      value: "C"
      name: "Cheque Diferido"
      description: "Cheque Diferido"
    }
  }
}
fields {
  uuid: "8d034cde-fb40-11e8-a479-7a0060f0aa01"
  name: "T\303\251rmino Pago OC"
  description: "T\303\251rmino de Pago en una Orden de Compra"
  help: "Las Condiciones de Pago de la OC indica los t\303\251rminos de pago que ser\303\241n usados cuando se llegue a facturar esta orden de compra"
  sequence: 90
  columnName: "PO_PaymentTerm_ID"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isSameLine: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 18
  reference {
    uuid: "a47ddbd6-fb40-11e8-a479-7a0060f0aa01"
    name: "C_PaymentTerm"
    validationType: "T"
    referenceTable {
      uuid: "2ea694c8-1f96-11e9-a7e9-83827d68753a"
      tableName: "C_PaymentTerm"
      keyColumnName: "C_PaymentTerm_ID"
      displayColumnName: "Name"
    }
  }
}
fields {
  uuid: "8ceb0a52-fb40-11e8-a479-7a0060f0aa01"
  name: "Lista de Precios de Compra"
  description: "Lista de precios usada por este Socio del Negocio"
  help: "Identifica la lista de precios usada por un proveedor para productos comprados por esta organizaci\303\263n."
  sequence: 100
  columnName: "PO_PriceList_ID"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 18
  reference {
    uuid: "a47de748-fb40-11e8-a479-7a0060f0aa01"
    name: "M_PriceList"
    validationType: "T"
    referenceTable {
      uuid: "2ea669bc-1f96-11e9-a7c7-c3e1567080e1"
      tableName: "M_PriceList"
      keyColumnName: "M_PriceList_ID"
      displayColumnName: "Name"
    }
  }
}
fields {
  uuid: "8cf417dc-fb40-11e8-a479-7a0060f0aa01"
  name: "Esquema Del Descuento en OC"
  description: "Esquema para calcular el porcentaje de descuento comercial en compra"
  sequence: 110
  columnName: "PO_DiscountSchema_ID"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isSameLine: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 18
  reference {
    uuid: "a47daee0-fb40-11e8-a479-7a0060f0aa01"
    name: "M_DiscountSchema not PL"
    validationType: "T"
    referenceTable {
      uuid: "2ea6bcd2-1f96-11e9-a837-7b20558cfdf4"
      tableName: "M_DiscountSchema"
      keyColumnName: "M_DiscountSchema_ID"
      displayColumnName: "Name"
      whereClause: "M_DiscountSchema.DiscountType<>\'P\'"
    }
  }
}
fields {
  uuid: "8d1f0604-fb40-11e8-a479-7a0060f0aa01"
  name: "Manufacturer"
  description: "Indicate role of this Business partner as Manufacturer"
  sequence: 120
  columnName: "IsManufacturer"
  isDisplayed: true
  isDisplayedGrid: true
  isAllowCopy: true
  isUpdateable: true
  isAllowLogging: true
  displayType: 20
  defaultValue: "\'N\'"
}
fields {
  uuid: "8db10658-fb40-11e8-a479-7a0060f0aa01"
  name: "AP Trial balance"
  description: "Trial Balance for a period or date range"
  help: "Select a Period (current period if empty) or enter a Account Date Range. If an account is selected, the balance is calculated based on the account type and the primary calendar of the client (i.e. for revenue/expense accounts from the beginning of the year). If no account is selected, the balance is the sum of all transactions before the selected account range or first day of the period selected. You can select an alternative Reporting Hierarchy."
  sequence: 160
  columnName: "APEnquiry"
  isDisplayed: true
  isAllowCopy: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 28
  process {
    uuid: "a42cfb4e-fb40-11e8-a479-7a0060f0aa01"
    name: "AP Trial balance"
    description: "Trial Balance for a period or date range"
    help: "Select a Period (current period if empty) or enter a Account Date Range. If an account is selected, the balance is calculated based on the account type and the primary calendar of the client (i.e. for revenue/expense accounts from the beginning of the year). If no account is selected, the balance is the sum of all transactions before the selected account range or first day of the period selected. You can select an alternative Reporting Hierarchy."
    isReport: true
  }
  identifierSequence: 160
}
fields {
  uuid: "8db0f564-fb40-11e8-a479-7a0060f0aa01"
  name: "Unapplied AR Payments"
  description: "Payment Detail Report"
  help: "Type adjusted payments (receipts positive, payments negative) with allocated and available amounts"
  sequence: 170
  columnName: "UnappliedPayments"
  isDisplayed: true
  isAllowCopy: true
  isSameLine: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 28
  process {
    uuid: "a42cfbb2-fb40-11e8-a479-7a0060f0aa01"
    name: "Unapplied AR Payments"
    description: "Payment Detail Report"
    help: "Type adjusted payments (receipts positive, payments negative) with allocated and available amounts"
    isReport: true
  }
  identifierSequence: 170
}
fields {
  uuid: "8db10de2-fb40-11e8-a479-7a0060f0aa01"
  name: "Open Invoices"
  description: "Open Item (Invoice) List"
  help: "Displays all unpaid invoices for a given Business Partner and date range. Please note that Invoices paid in Cash will appear in Open Items until the Cash Journal is processed."
  sequence: 180
  columnName: "VendorOpenInvoices"
  isDisplayed: true
  isAllowCopy: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 28
  process {
    uuid: "a42cf996-fb40-11e8-a479-7a0060f0aa01"
    name: "Open Invoices"
    description: "Open Item (Invoice) List"
    help: "Displays all unpaid invoices for a given Business Partner and date range. Please note that Invoices paid in Cash will appear in Open Items until the Cash Journal is processed."
    isReport: true
  }
  identifierSequence: 180
}
fields {
  uuid: "8db10130-fb40-11e8-a479-7a0060f0aa01"
  name: "Not Posted Invoice"
  sequence: 190
  columnName: "NotPosted"
  isDisplayed: true
  isAllowCopy: true
  isSameLine: true
  isUpdateable: true
  isAllowLogging: true
  displayLogic: "@IsVendor@=\'Y\'"
  displayType: 28
  process {
    uuid: "a42cff4a-fb40-11e8-a479-7a0060f0aa01"
    name: "Not Posted Invoice"
    isReport: true
  }
  identifierSequence: 190
}
</pre>
- Request Field
<pre>
ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient main
INFORMACIÓN: ####################### Field Only #####################
ene 24, 2019 5:12:12 PM org.spin.grpc.util.DictionaryClient requestField
INFORMACIÓN: Field uuid: "8cecee3a-fb40-11e8-a479-7a0060f0aa01"
name: "Regla de Pago"
description: "Opci\303\263n de pago por compras"
help: "La Regla de Pago indica el m\303\251todo de pago de las compras"
sequence: 80
columnName: "PaymentRulePO"
isDisplayed: true
isDisplayedGrid: true
isAllowCopy: true
isUpdateable: true
isAllowLogging: true
displayLogic: "@IsVendor@=\'Y\'"
displayType: 17
reference {
  uuid: "a47e0606-fb40-11e8-a479-7a0060f0aa01"
  name: "_Payment Rule"
  validationType: "L"
  values {
    uuid: "a456980a-fb40-11e8-a479-7a0060f0aa01"
    value: "K"
    name: "Tarjeta De Cr\303\251dito"
  }
  values {
    uuid: "a4569878-fb40-11e8-a479-7a0060f0aa01"
    value: "P"
    name: "A Cr\303\251dito"
  }
  values {
    uuid: "a45698dc-fb40-11e8-a479-7a0060f0aa01"
    value: "S"
    name: "Cheque"
  }
  values {
    uuid: "a4569940-fb40-11e8-a479-7a0060f0aa01"
    value: "T"
    name: "Dep\303\263sito Directo"
  }
  values {
    uuid: "a4597c5a-fb40-11e8-a479-7a0060f0aa01"
    value: "B"
    name: "Efectivo"
  }
  values {
    uuid: "a4597cc8-fb40-11e8-a479-7a0060f0aa01"
    value: "D"
    name: "D\303\251bito Directo"
  }
  values {
    uuid: "a4597d2c-fb40-11e8-a479-7a0060f0aa01"
    value: "M"
    name: "Mezclado"
  }
  values {
    uuid: "2ea2273a-1f96-11e9-a15f-8f58dba465bb"
    value: "C"
    name: "Cheque Diferido"
    description: "Cheque Diferido"
  }
}
</pre>
