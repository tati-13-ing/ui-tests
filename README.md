# OTC UI Tests

Автоматизированные UI-тесты поиска товаров на сайте OTC, реализованные на Java с использованием Selenide, JUnit 5 и Maven.

Тест выполняет поиск товара, собирает названия и цены найденных товаров и сохраняет результат в текстовый файл.

## Технологии

* Java 17
* Maven
* Selenide
* Selenium WebDriver
* JUnit 5
* Google Chrome

## Требования

Перед запуском установите:

* JDK 17 или новее;
* Maven;
* Google Chrome;
* IntelliJ IDEA или другую Java IDE.

Проверить установку:

```bash
java -version
mvn -version
google-chrome --version
```

## Запуск тестов

Все команды необходимо выполнять из корневой папки проекта.

### Запустить все тесты

```bash
mvn test
```

### Очистить проект и запустить все тесты

```bash
mvn clean test
```

### Запустить тесты без открытия окна браузера

```bash
mvn clean test -Dheadless=true
```

### Запустить только класс OtcSearchTest

```bash
mvn test -Dtest=OtcSearchTest
```

## Результат выполнения

После успешного запуска найденные товары сохраняются в файл:

```text
results/products.txt
```
