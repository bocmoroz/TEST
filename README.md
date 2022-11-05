# warehouse-rest-app
Тестовое задание 

## Установка

Использовать [maven](https://maven.apache.org/download.cgi) для сборки проекта.<br/>
Для этого в корневой папке проекта с pom.xml файлом выполнить:

```
mvn clean install
```

## Использование
Возможные способы использования:<br/>
1. Запустить в IDE метод main() класса Application.
2. В папке, содержащей .jar файл выполнить:

```
java -jar Warehouse_rest_app-1.0-SNAPSHOT.jar
```

При запуске по умолчанию используется create-drop. Если такая конфигурация не подходит, то имеется файл [data.sql](src/main/resources/data.sql) для ининциализации.

Документация swagger доступна по URL: http://localhost:8080/swagger-ui.html.

UML диграмма связей в БД: [UML Diagram](src/main/resources/UML_Diagram.png)
