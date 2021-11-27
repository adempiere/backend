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

## Include ADempiere dependencies

In the build.gradle you need to include in the next sections the references to jars placed in lib, packages and zkpackages in your ADEMPIERE _HOME

* In the repository section as flatDir
Example:
  ```
  flatDir {
        dirs $ad_path + '/lib'
  }
  ```
* In de dependencie section,
  Example
    ```
    implementation fileTree(dir: $ad_path + '/lib', include: '*.jar')
    ```

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
Don't forgive that for run server you need set yaml file line is /resources folder.

- Use latest [release](https://github.com/erpcya/adempiere-gRPC-Server/releases)
- Unzip binary
- go to bin folder
- run it

```Shell
./adempiere-all-in-one-server "/tmp/dictionary_connection.yaml"
```

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
