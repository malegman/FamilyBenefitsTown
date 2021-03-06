# FamilyBenefitsTown
Rest-API сервиса "Пособия для семьи с детьми", часть с городом.

Вспомогательный проект для проекта FamilyBenefits. Создан для разработки части администрирования сервиса, фильтрации запросов, взаимодействия с бд.

#### Взаимодействие пользователя с сервисом. Администрирование.
В рамках данного проекта пользователь сможет зарегистрироваться, получить данные о себе и о городах. Пользователь с ролью администратора может редактировать города.
В системе так же есть роль супер-администратора, который может выдавать и забирать роль администратора. Только один пользователь может иметь роль супер-администратора.

#### Безопасность. Аутентификация и авторизация.
Все идентификаторы в системе представляют собой строку, сгенерированную криптостойким ГПСЧ, длиной 20 символов. Исключение - ИД ролей, которые фиксированные для упрощения работы.
Вход в систему осуществляется по логину (email) и коду (число из 6 цифр), который высылается на почту пользователя при запросе на вход.
Для обеспечения безопасности сессии пользователя используются токены досутпа и восстановления. Пара токенов выдается в момент успешного входа в систему по почте и коду.
Токен восстановления хранится в бд и необходим для отслеживания присутствия пользователя в системе, не был ли осуществлен выход, и для обновления пары токенов.
Токен восстановления представляет собой строку длиной 50 символов, сгенерированную криптостойким ГПСЧ.
В роли токена доступа используется JWT. В полезной нагрузке токена сохраняются ИД пользователя и его роли. Необходим для проверки прав пользователя.
На каждый запрос, требующий авторизации, необходимо предоставлять оба токена. Если токен доступа просрочен, то он и токен восстановления сразу же обновляются. Если токен восстановления просрочен, то происходит выход из системы.

Проект в стадии тестирования микро-сервисов.
