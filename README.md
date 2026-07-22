# OTC UI Tests

Учебный проект автоматизированного UI-тестирования каталога товаров сайта OTC.

Тесты запускаются локально из IntelliJ IDEA, а браузер Google Chrome и ChromeDriver работают удалённо в Docker-контейнере с Selenium Grid.


## Проверяемый сценарий

Автоматизированный тест выполняет следующие действия:

1. открывает сайт OTC;
2. переходит в каталог товаров;
3. выбирает город Краснодар;
4. вводит поисковый запрос «Принтер»;
5. выполняет поиск;
6. получает названия и цены найденных товаров;
7. собирает результаты с первой и второй страниц;
8. сохраняет найденные товары в текстовый файл;
9. проверяет создание файла;
10. проверяет, что файл не пустой;
11. проверяет соответствие содержимого файла найденным товарам.

Результат сохраняется в:

```text
results/products.txt
```

## Используемые технологии

- Java 17;
- Maven;
- JUnit 5;
- Selenide;
- Selenium WebDriver;
- RemoteWebDriver;
- Selenium Grid;
- Google Chrome;
- ChromeDriver;
- Logback;
- SLF4J;
- Docker;
- Docker Compose;
- Xvfb;
- VNC;
- noVNC;
- IntelliJ IDEA.

## Требования

Перед запуском необходимо установить:

- JDK 17;
- IntelliJ IDEA;
- Docker Engine;
- Docker Compose.

Отдельно устанавливать Google Chrome и ChromeDriver для теста не требуется.

Проверить Java:

```bash
java -version
```

Проверить Docker:

```bash
docker --version
docker compose version
```
При первом запуске выполните:

```bash
docker compose pull
```

Запустить контейнер:

```bash
docker compose up -d
```

Проверить состояние:

```bash
docker compose ps
```


После запуска контейнера открыть:

```text
http://127.0.0.1:7900/?autoconnect=1&resize=scale&password=secret
```
Запустить тест
```bash
mvn clean test
```