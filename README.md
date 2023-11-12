[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
<img src="src/main/resources/static/images/logo_transparent.png" width="200" align="right" />

# Клюкарник
Целта на тази задача е да създадем един клюкарник, където всеки може да праща клюкини и да следи клюкините на своите приятели. (Подобие на Twitter)

## Компилиране
За компилиране на проекта се нуждаете от maven.

## REST API спецификация
След стартиране на проекта, отворете http://localhost:8080/api/index.html
за да видите спецификация на REST API, които трябва да направите.

## Изисквания
1. Направете fork на repository-то за вашият екип.
1. Приложението трябва да работи, както е описано във REST API спецификацията по-горе.
1. `mvn verify` трябва да минава без проблеми (проекта е конфигуриран да изисква 50% code coverage).
1. Spotbugs е maven plugin, който ще ви помогне да създадете качествен код. Като част от условието на задачата е този плъгин да не отчита проблеми в кода.
1. Можете да добавяте колко си искате още библиотеки, и maven plugins, но без да премахвате вече избраните.
1. Кода трябва да е форматиран използвайки [Google Style Guide](https://github.com/google/styleguide/)

## Няколко съвета
1. Огранизирайте кода в пакети, които са стандартни са Spring -
config, controller, dto, exception, model, repository, security, service, util, validation.
1. Постарайте се бизнес логиката да е изцяло в services, а контролерите да се грижат
единствено за приемане на requests и предоставяне на данните в нужният формат.
1. Опитайте се да постигнете максимално ниво на code coverage. Лесно ли е?
1. Изпробвайте различните видове тестове - unit, mock, integration, etc.
1. Подсигурете се, че всички входни данни са валидирани.
1. Не забравяйте да използвате коментари в кода, там, където сте се затруднили - тези
коментари са като книга, която след време можете отново да прочете.
1. Да разпределите задачите по начин, че всеки може да работи по отделна част от проекта, без
да си пречите твърде много. Помислете как да проследявате прогреса.
1. Спазвайте дисциплина при commit - използвайте достатъчно описателни commit messages.
1. Използвайте Pull/Merge Requests за да се учите от другите. Нека винаги някой друг
направи review на вашият код. Не merge-вайте промените никога сам.

## Бележки
[api.yml](src/main/resources/static/api.yml) - документацията на REST API е създадено с помощта на http://editor.swagger.io. 
Всъщност се използва [OpenAPI](https://swagger.io/docs/specification/about/) формат, който
има не-малка поддръжка в лицето на [OpenAPITools](https://github.com/OpenAPITools)

## Web Приложение
Web приложението е достъпно като отворите http://localhost:8080 в web browser.

To е разработено с помощта на mock server. За стартирането му е нужно:
1. Да имате инсталиран docker.
2. Да пуснете следният команден ред в `src/main/resources/static`:
```shell script
docker run -p 8000:8000 -v `pwd`/api.yml:/api.yml danielgtaylor/apisprout --validate-server /api.yml  
```


## Документация
За информация относно използваните библиотеки и инструменти:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/maven-plugin/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)

## Ръководства
Следните ръководства илюстрират как да използвате Spring:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

## Пригответе се за бъдещето
REST API макар и доста разпространено, не е най-добрият вариант за Micro-services.
Много по-ефикастно е да ползвате gRPC. Всъщност gRPC може да използва HTTP/2 протокола
като това го прави много близък до REST и е възможно да вървят ръка за ръка.

Ето и няколко полезни статии по въпроса:
* https://medium.com/apis-and-digital-transformation/openapi-and-grpc-side-by-side-b6afb08f75ed
* https://cloud.google.com/apis/design/
* https://www.baeldung.com/spring-rest-api-with-protocol-buffers
