# Подбор животного
## 16 вариант
#### Источник
- ИБ - бесконечный
- ИЗ2 - равномерный
#### Прибор
- ПЗ1 - экспоненциальный
#### Дисциплины буфферизации
- Д10З1 - по кольцу
#### Дисциплина отказа
- Д10О3 - самая старая в буфере
#### Дисциплины постановки на обслуживание
- Д2П2 - по кольцу
- Д2Б2 - LIFO
#### Автоматический режим
- ОР1 - сводная таблица результатов
#### Пошаговый режим
- ОД1 - календарь событий, буфер и текущее состояние
####
- Д10З3 - положить в буфер по кольцу
- Д2П2 - получить из прибора по кольцу
- Д2Б2 - получить заявку LIFO
####
- Dequeue - есть очередь. 
- обрабатываем девайсом самую новую заявку - начало очереди, а отменяем самую старую - конец очереди.
# aps_pozdnova
