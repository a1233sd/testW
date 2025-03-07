# Запуск и тестирование

## Поднятие приложения

- **В Docker:**  
  Выполните команду в терминале:
  ```bash
  docker-compose up --build
  ```  
  Это поднимет контейнеры с PostgreSQL, приложением и pgAdmin.


- **Тестирование в Docker:**
  ```bash
  docker-compose --profile test up --build app-test
  ```

Эти инструкции помогут быстро поднять систему и выполнить тесты в нужной среде.