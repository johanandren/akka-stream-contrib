/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.stream.contrib.amqp

import java.util.Optional

import scala.collection.JavaConverters._
import scala.collection.immutable.Seq
import scala.compat.java8.OptionConverters._

/**
 * Internal API
 */
sealed trait AmqpConnectorSettings {
  def connectionSettings: AmqpConnectionSettings
  def declarations: Seq[Declaration]
}

sealed trait AmqpSourceSettings extends AmqpConnectorSettings

object NamedQueueSourceSettings {
  /**
   * Java API
   */
  def create(
    connectionSettings: AmqpConnectionSettings,
    queue:              String,
    declarations:       java.util.List[Declaration]
  ) =
    NamedQueueSourceSettings(connectionSettings, queue, declarations.asScala.toVector)

  /**
   * Java API
   */
  def create(
    connectionSettings: AmqpConnectionSettings,
    queue:              String,
    declarations:       java.util.List[Declaration],
    noLocal:            Boolean,
    exclusive:          Boolean,
    consumerTag:        String,
    arguments:          java.util.Map[String, AnyRef]
  ) =
    NamedQueueSourceSettings(
      connectionSettings,
      queue,
      declarations.asScala.toIndexedSeq,
      noLocal,
      exclusive,
      consumerTag,
      arguments.asScala.toMap
    )
}

final case class NamedQueueSourceSettings(
  connectionSettings: AmqpConnectionSettings,
  queue:              String,
  declarations:       Seq[Declaration],
  noLocal:            Boolean                = false,
  exclusive:          Boolean                = false,
  consumerTag:        String                 = "default",
  arguments:          Map[String, AnyRef]    = Map.empty
) extends AmqpSourceSettings

object TemporaryQueueSourceSettings {
  /**
   * Java API
   */
  def create(
    connectionSettings: AmqpConnectionSettings,
    exchange:           String,
    declarations:       java.util.List[Declaration],
    routingKey:         Optional[String]
  ) =
    TemporaryQueueSourceSettings(connectionSettings, exchange, declarations.asScala.toVector, routingKey.asScala)
}

final case class TemporaryQueueSourceSettings(
  connectionSettings: AmqpConnectionSettings,
  exchange:           String,
  declarations:       Seq[Declaration],
  routingKey:         Option[String]         = None
) extends AmqpSourceSettings

object AmqpSinkSettings {
  /**
   * Java API
   */
  def create(
    connectionSettings: AmqpConnectionSettings,
    exchange:           Optional[String],
    routingKey:         Optional[String],
    declarations:       java.util.List[Declaration]
  ) =
    AmqpSinkSettings(connectionSettings, exchange.asScala, routingKey.asScala, declarations.asScala.toVector)
}

final case class AmqpSinkSettings(
  connectionSettings: AmqpConnectionSettings,
  exchange:           Option[String],
  routingKey:         Option[String],
  declarations:       Seq[Declaration]
) extends AmqpConnectorSettings {
}

/**
 * Only for internal implementations
 */
sealed trait AmqpConnectionSettings

case object DefaultAmqpConnection extends AmqpConnectionSettings {
  /**
   * Java API
   */
  def getInstance: AmqpConnectionSettings = this
}

final case class AmqpConnectionUri(uri: String) extends AmqpConnectionSettings

object AmqpConnectionDetails {

  /**
   * Java API
   */
  def create(host: String, port: Int) = AmqpConnectionDetails(host, port)

  /**
   * Java API
   */
  def create(host: String, port: Int, credentials: Optional[AmqpCredentials], virtualHost: Optional[String]) =
    AmqpConnectionDetails(host, port, credentials.asScala, virtualHost.asScala)

}

final case class AmqpConnectionDetails(
  host:        String,
  port:        Int,
  credentials: Option[AmqpCredentials] = None,
  virtualHost: Option[String]          = None
) extends AmqpConnectionSettings
final case class AmqpCredentials(username: String, password: String) {
  override def toString = s"Credentials($username, ********)"
}

sealed trait Declaration

object QueueDeclaration {
  /**
   * Java API
   */
  def create(
    name:       String,
    durable:    Boolean,
    exclusive:  Boolean,
    autoDelete: Boolean,
    arguments:  java.util.Map[String, AnyRef]
  ): QueueDeclaration =
    QueueDeclaration(name, durable, exclusive, autoDelete, arguments.asScala.toMap)

  /**
   * Java API
   */
  def create(name: String): QueueDeclaration = QueueDeclaration(name)

}

final case class QueueDeclaration(
  name:       String,
  durable:    Boolean             = false,
  exclusive:  Boolean             = false,
  autoDelete: Boolean             = false,
  arguments:  Map[String, AnyRef] = Map.empty
) extends Declaration

object BindingDeclaration {
  /**
   * Java API
   */
  def create(name: String, exchange: String): BindingDeclaration = BindingDeclaration(name, exchange)

  /**
   * Java API
   */
  def create(
    queue:      String,
    exchange:   String,
    routingKey: Optional[String],
    arguments:  java.util.Map[String, AnyRef]
  ): BindingDeclaration =
    BindingDeclaration(queue, exchange, routingKey.asScala, arguments.asScala.toMap)
}

final case class BindingDeclaration(
  queue:      String,
  exchange:   String,
  routingKey: Option[String]      = None,
  arguments:  Map[String, AnyRef] = Map.empty
) extends Declaration

object ExchangeDeclaration {

  /**
   * Java API
   */
  def create(name: String, exchangeType: String): ExchangeDeclaration =
    ExchangeDeclaration(name, exchangeType)

  /**
   * Java API
   */
  def create(
    name:         String,
    exchangeType: String,
    durable:      Boolean,
    autoDelete:   Boolean,
    internal:     Boolean,
    arguments:    java.util.Map[String, AnyRef]
  ): ExchangeDeclaration =
    ExchangeDeclaration(name, exchangeType, durable, autoDelete, internal, arguments.asScala.toMap)
}

final case class ExchangeDeclaration(
  name:         String,
  exchangeType: String,
  durable:      Boolean             = false,
  autoDelete:   Boolean             = false,
  internal:     Boolean             = false,
  arguments:    Map[String, AnyRef] = Map.empty
) extends Declaration