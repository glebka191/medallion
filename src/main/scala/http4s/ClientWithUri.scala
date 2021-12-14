package http4s

import dm.Client
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class ClientWithUri(id: Long, url: String, name: String, email: String, password: String)

object ClientWithUri{

  def apply(basePath: String,client: Client): ClientWithUri = ClientWithUri(
    client.id.value,
    s"$basePath/${client.id.value}",
    client.clientLoad.name,
    client.clientLoad.email,
    client.clientLoad.password
  )

  implicit val encoder: Encoder[ClientWithUri] = deriveEncoder
  implicit val decoder: Decoder[ClientWithUri] = deriveDecoder
}