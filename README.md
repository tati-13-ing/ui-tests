# OTC UI Tests

Учебный проект автоматизированного UI-тестирования каталога товаров сайта OTC.

Проект полностью контейнеризирован. Java-код тестов, Maven, JUnit 5 и Selenide запускаются в одном Docker-контейнере, а Selenium Grid, ChromeDriver и Google Chrome — в другом.

Локальный запуск тестов через установленную на компьютере Java, Maven или Chrome не используется.

## Проверяемый сценарий

Автоматизированный тест выполняет следующие действия:

1. открывает сайт OTC;
2. переходит в каталог товаров;
3. выбирает город Краснодар;
4. вводит поисковый запрос «Принтер»;
5. выполняет поиск;
6. получает названия и цены найденных товаров;
7. собирает результаты с первой и второй страниц;
8. сохраняет полученные данные в текстовый файл;
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
- Selenium Grid;
- Google Chrome;
- ChromeDriver;
- Docker;
- Docker Compose;
- Xvfb;
- VNC;
- noVNC;
- SLF4J Simple.

## Требования

Для запуска проекта необходимы только:

- Docker Engine;
- Docker Compose;
- доступ к интернету.

Проверить Docker:

```bash
docker --version
docker compose version
```

## Сборка контейнера тестов

Собрать образ сервиса `tests`:

```bash
docker compose build tests
```

Сначала запустить Selenium:

```bash
docker compose up -d selenium-chrome
```

Затем открыть в браузере:

```text
http://127.0.0.1:7900/?autoconnect=1&resize=scale&password=secret
```

После подключения запустить тест:

```bash
docker compose run --rm tests
```
