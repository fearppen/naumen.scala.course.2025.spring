package ru.dru

import zio.{IO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

import java.io.{BufferedReader, BufferedWriter, FileReader, FileWriter}


/**
 * Необходимо реализовать функции readData и writeData, записывающие и читающие данные в/из файла соответственно.
 * В реализации следует применять безопасное использование ресурсов ZIO.acquireReleaseWith
 */


object ResuourceTraining extends ZIOAppDefault {

  def readData(filePath: String): IO[Throwable, String] = ZIO.acquireReleaseWith(
    ZIO.attempt(new BufferedReader(new FileReader(filePath)))
  )(reader => ZIO.succeed(reader.close())
  ) {reader => ZIO.attempt {
    val result = new StringBuilder()
    var line = reader.readLine()
    while (line != null) {
      result.append(line)
      line = reader.readLine()
    }
    result.toString()
  }}

  def writeData(filePath: String, data: String): ZIO[Any, Nothing, Unit] = {
    ZIO.acquireReleaseWith(
      ZIO.attempt(new BufferedWriter(new FileWriter(filePath)))
    )(writer => ZIO.succeed(writer.close())
    ) {writer => ZIO.attempt {
      writer.write(data)
      writer.flush()
    }}.orDie
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    writeData("text.txt", "text text text text\ntext text text text\ntext text text text")
    val data = readData("text.txt")
    ZIO.succeed(println(data))
  }
}
