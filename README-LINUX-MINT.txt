OTC UI TESTS — запуск на Linux Mint

Необходимое:
1. OpenJDK 17
2. IntelliJ IDEA
3. Google Chrome
4. Интернет при первом запуске для загрузки Maven-зависимостей и ChromeDriver
5. Maven и Git — необязательны для запуска из IntelliJ IDEA, но полезны для терминала и GitHub

Установка системных пакетов:
  sudo apt update
  sudo apt install openjdk-17-jdk maven git unzip

Проверка:
  java -version
  javac -version
  mvn -version
  google-chrome --version

Открытие проекта:
1. Запустить IntelliJ IDEA.
2. Нажать Open.
3. Выбрать файл pom.xml в корне этой папки.
4. Нажать Open as Project.
5. Дождаться завершения загрузки Maven-зависимостей.
6. File -> Project Structure -> Project SDK -> выбрать JDK 17.
7. Открыть src/test/java/ru/example/otc/OtcSearchChromeTest.java.
8. Нажать зелёный треугольник рядом с классом или методом теста.

Запуск из терминала из корня проекта:
  mvn -Dtest=OtcSearchChromeTest test

Запуск без отображения окна браузера:
  mvn -Dtest=OtcSearchChromeTest -Dheadless=true test

Результат сохраняется в:
  results/products.txt

Папка results будет создана тестом автоматически.
