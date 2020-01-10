# DigitReader

## Требования для сборки:
* Android Studio
* Работающий эмулятор android-смартфона либо подключенный к IDE android-смартфон
## Сборка:
* Android Studio -> checkout project from version control
* Дождаться завершения выполнения Gradle скриптов
* Выбрать для запуска подключенный смартфон или эмулятор, нажать кнопку запуска

## Ход разработки:
Нейронная сеть используемая в приложении была обучена по руководству MNIST Google colab: 
https://github.com/KazankovMarch/mnist (на ЯП python с использованием библиотек tensorflow).
Модель была сконвертирована из .h5 формата в .tflite c помощью командной строки:
```
tflite_convert \
  --output_file=/tmp/foo.tflite \
  --keras_model_file=/tmp/keras_model.h5
```
Модель загружается в Java код приложения посредством библиотеки `org.tensorflow.lite` (https://www.tensorflow.org/lite/guide/inference)

За основу android-приложения были взяты исходники
https://github.com/googlesamples/android-vision/tree/master/visionSamples/barcode-reader
и библиотеки `com.google.android.gms:play-services-vision`

Основная логика распознавания цифр сосредоточена в классе `DigitDetector`, расширяющего абстрактный класс `Detector<Digit>`.
В нем принимается изображение с камеры смартфона, наибольший квадратный регион пикселей этого изображения сжимается до 28х28 пикселей, значения которых нормализованы (от нуля до единицы).
Затем этот массив отправляется в обучненную модель, а результат выводится на экран
