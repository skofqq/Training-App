# Тренировки — Training App

Нативное Android-приложение (Kotlin + Jetpack Compose) с программой тренировок на два дня.
Порт самодостаточной веб-страницы `sport.html` в полноценное приложение с сохранением прогресса.

## Возможности

- Две тренировки с переключением по вкладкам
- Карточки упражнений: подходы, вес, иллюстрация техники
- Раскрывающиеся блоки: **Настройка**, **Как делать**, **Чего не делать**
- Отметка выполненных подходов по каждому упражнению и общий счётчик прогресса
- Прогресс сохраняется между запусками (`SharedPreferences`)
- Тёплая «бумажная» тема, повторяющая дизайн исходной страницы

## Стек

- Kotlin, Jetpack Compose (Material 3)
- `minSdk` 29, `targetSdk` 36
- Хранение прогресса — `SharedPreferences`

## Сборка

```bash
./gradlew :app:assembleDebug
```

APK окажется в `app/build/outputs/apk/debug/app-debug.apk`.

Готовый APK также прикладывается к каждому [релизу](https://github.com/skofqq/Training-App/releases).

## Структура

| Файл | Назначение |
|------|------------|
| `app/src/main/java/com/atixcg/training/Data.kt` | Модели и данные упражнений (обе тренировки) |
| `app/src/main/java/com/atixcg/training/MainActivity.kt` | Весь UI на Compose |
| `app/src/main/java/com/atixcg/training/ProgressStore.kt` | Сохранение прогресса |
| `app/src/main/java/com/atixcg/training/ui/theme/` | Палитра и тема |
| `app/src/main/res/drawable/ex_*.webp` | Иллюстрации упражнений |
