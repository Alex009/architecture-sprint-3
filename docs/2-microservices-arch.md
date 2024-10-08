# Разделение на микросервисы

1. User Service
2. Home Management Service
3. Device Integration Services
    1. MQTT Device Adapter
    2. Other Device Adapter
4. Device Management Service
5. Telemetry Service
6. Automation Service
7. Notification Service
8. API Gateway
9. Шина данных

Новая диаграмма контекста - [2-c4-context-new.puml](2-c4-context-new.puml)

Диаграмма контейнеров - [2-c4-containers.puml](2-c4-containers.puml)

## Детализация

## User Service (Сервис пользователей)

Основные функции:

- Регистрация и авторизация пользователей.
- Управление данными пользователей.

Для данного сервиса будет использоваться готовое opensource решение - https://www.keycloak.org . Он выполняет все нужные
нам
функции, а также позволяет расширять в дальнейшем функционал - добавлять другие способы входа (например через соцсети),
масштабироваться в кластере, при необходимости можно будет сделать свой адаптер с собственной логикой.

## Home Management Service (Сервис управления домами)

Основные функции:

- Управление домами пользователей (создание, редактирование, удаление).
- Управление доступом пользователей к домам.

Взаимодействие:

- Предоставление информации о доступе пользователя к дому для других сервисов.

Это будет разработанный нашей компанией сервис.

Диаграмма компонентов - [2-c4-components-home.puml](2-c4-components-home.puml)

## Сервисы интеграции с конкретными типами устройств

Основные функции:

- Единый стандартный API интерфейс для работы адаптеров конечных устройств.
- Сервисы-адаптеры отправляют стандартизированные сообщения по своему устройству, так и принимают стандартизированные
  команды для отправки устройству.

Это будут разработанные нашей компанией сервисы-адаптеры.

Принцип работы на диаграмме последовательности - [2-integration-sequence.puml](2-integration-sequence.puml)

Сервисы интеграции сохраняют в свою собственную Redis базу данных технические данные подключаемых устройств и связь с
системным internal_device_id, которым будут оперировать все сервисы. Информация о том какой именно тип устройство имеет
остается только в бд самого сервиса интеграции с данным типом устройств.

В зависимости от типов устройств в интеграционных сервисах может меняться логика внутренней работы и объем хранимых об
устройстве данных, но апи взаимодействия с остальной системой - остается общим.

Для команд предполагается фиксированный набор:

1. включить
2. выключить
3. изменить настройку(параметр, значение)

Получение списка доступных параметров предполагается в момент регистрации, когда устройство будет подключено к сервису.
В ответе в топике /devices/register-success в событии успеха подключения этого internal_device_id будет список доступных
параметров, модель/имя устройства (технические данные).

Для сервиса интеграции устройств internal_device_id является данными приходящими из вне - его сгенерирует devices
management service при попытке пользователя подключить устройство.

Диаграмма компонентов типового сервиса интеграции - [2-c4-components-integration.puml](2-c4-components-integration.puml)

### Сервис-адаптер для работы с текущими устройствами

Адаптер для работы с текущими используемыми пользователями устройствами отопительной систем

### Сервис-адаптер для работы с новыми устройствами

Адаптер для подключения нового, нужного для бизнеса устройства. Таких адаптеров будет множество - под разные протоколы
свой адаптер.

## Device Management Service (Сервис управления устройствами)

Основные функции:

- Управление устройствами в доме (добавление, удаление, настройка).
- Хранение информации о подключенных устройствах и связи с домами.
- Взаимодействие с сервисами-адаптерами для работы с устройством.

Взаимодействие:

- Предоставление информации о доступе пользователя к устройству для других сервисов.
- Получение информации о доступе пользователя к дому от сервиса управления домами.

Это будет разработанный нашей компанией сервис.

Диаграмма компонентов - [2-c4-components-devices.puml](2-c4-components-devices.puml)

Диаграмма процесса проверки доступа пользователя к устройству - [2-devices-permissions-sequence.puml](2-devices-permissions-sequence.puml) 

## Telemetry Service (Сервис телеметрии)

Основные функции:

- Сбор и хранение данных с устройств-датчиков.
- Предоставление исторических данных пользователям.
- Взаимодействие с сервисами-адаптерами для чтения данных с устройств.

Взаимодействие:

- Интеграция с шиной данных (Kafka) для получения данных в режиме реального времени.
- Получение информации о доступе пользователя к устройству от сервиса управления устройствами.

Это будет разработанный нашей компанией сервис.

Диаграмма компонентов - [2-c4-components-telemetry.puml](2-c4-components-telemetry.puml)

## Automation Service (Сервис автоматизации)

Основные функции:

- Управление сценариями автоматизации.
- Обработка событий и выполнение сценариев на основе данных телеметрии.

Взаимодействие:

- Отправка команд в Device Management Service.
- Получение данных из Telemetry Service.

Это будет разработанный нашей компанией сервис.

Диаграмма компонентов - [2-c4-components-automation.puml](2-c4-components-automation.puml)

## Notification Service (Сервис уведомлений)

Основные функции:

- Интеграция с внешними системами для отправки SMS, email.

Взаимодействие:

- Получение событий от Automation Service и других сервисов через шину данных (Kafka).

Это будет разработанный нашей компанией сервис, с внешними интеграциями - SMS шлюз и email шлюз.

Был рассмотрен вариант использования готовых решений, но не было выбрано такого, потому что:

1. OpenSource решения с возможностью развернуть в своём кластере (для полного контроля работоспособности и защиты от
   санкционных рисков) не позволяют решить задачу SMS и Email - решается только одна из них и большинство предлагаемого
   функционала не будет использоваться, а значит трудоемкость изучения решения выше чем польза.
2. Облачные решения - внутри страны нет решения работающего и с SMS и с Email, а зарубежные отброшены по причинам
   санкционных рисков.

А также собственное решение позволит предоставить для других микросервисов наиболее удобный способ взаимодействия для
решения задач нашей системы.

Диаграмма компонентов - [2-c4-components-notification.puml](2-c4-components-notification.puml)

## API Gateway

Основные функции:

- Маршрутизация запросов к нужным микросервисам.
- Проверка валидности JWT токена перед передачей запроса микросервисам

Взаимодействие:

- Интерфейс между внешними клиентами (например, веб-интерфейсом) и микросервисами.

Это будет готовое opensource решение - https://konghq.com

В данный момент, судя по ридми проекта, используется https://kusk.io , но при рассмотрении сделан вывод что строгая
завязка на OpenAPI может стать минусом с развитием и масштабированием. Настройки gateway будут смешаны с OpenAPI, что
усложнит понимание конфигурации Gateway, а также каждый микросервис предоставляет свою OpenAPI спецификацию, что может
создать конфликты и упущенные важные моменты (например в одном из сервисов разработчик забудет указать необходимость
проверки авторизации на запросе). Kong выглядит более масштабируемым решением - конфигурация у него своя и раз API
Gateway у нас централизованный сервис, то нам нужно контролировать его настройки в максимально прозрачном виде.

## Шина данных

Обеспечение асинхронного взаимодействия между микросервисами.

Это будет готовое opensource решение - https://kafka.apache.org

