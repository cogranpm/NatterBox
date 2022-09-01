package com.parinherm.audio

import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import javax.sound.sampled.*


class AudioRecognizer {

    init {
        LibVosk.setLogLevel(LogLevel.DEBUG)
    }

    fun run(){

        Model("D:\\shared\\Source\\kotlin\\resources\\vosk-model-small").use { model ->
            AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream(
                "D:\\shared\\Source\\kotlin\\resources\\vosk.wav")))
                .use { ais ->
                    Recognizer(model, 16000f).use { recognizer ->
                        var nbytes: Int
                        val b = ByteArray(4096)
                        while (ais.read(b).also { nbytes = it } >= 0) {
                            if (recognizer.acceptWaveForm(b, nbytes)) {
                                println(recognizer.result)
                            } else {
                                println(recognizer.partialResult)
                            }
                        }
                        println(recognizer.finalResult)
                    }
                }
        }
    }

    fun runCapture() {
        val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 60000f, 16, 2, 4, 44100f, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        var microphone: TargetDataLine
        var speakers: SourceDataLine

        Model("D:\\shared\\Source\\kotlin\\resources\\vosk-model-small").use { model ->

            Recognizer(model, 120000f).use { recognizer ->
                microphone = AudioSystem.getLine(info) as TargetDataLine
                microphone.open(format)
                microphone.start()

                val out = ByteArrayOutputStream()
                var numBytesRead: Int
                val CHUNK_SIZE = 1024
                var bytesRead = 0

                val dataLineInfo: DataLine.Info = DataLine.Info(SourceDataLine::class.java, format)
                speakers = AudioSystem.getLine(dataLineInfo) as SourceDataLine
                speakers.open(format)
                speakers.start()
                val b = ByteArray(4096)

                while (bytesRead <= 100000000) {
                    numBytesRead = microphone.read(b, 0, CHUNK_SIZE)
                    bytesRead += numBytesRead
                    out.write(b, 0, numBytesRead)
                    speakers.write(b, 0, numBytesRead)
                    if (recognizer.acceptWaveForm(b, numBytesRead)) {
                        System.out.println(recognizer.result)
                    } else {
                        System.out.println(recognizer.partialResult)
                    }
                }
                System.out.println(recognizer.finalResult)
                speakers.drain()
                speakers.close()
                microphone.close()
            }
        }
    }
}