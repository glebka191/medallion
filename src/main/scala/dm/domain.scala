package dm

import io.circe.Decoder
import io.circe.generic.semiauto._


final case class ClientLoad(name: String, email: String, password: String)

final case class Client(id: ClientId, clientLoad: ClientLoad) {

  def update(up: ClientPatchForm): Client ={
    this.copy(
      id = this.id,
      clientLoad = clientLoad.copy(
        name = up.nameUp.getOrElse(clientLoad.name),
        email = up.emailUp.getOrElse(clientLoad.email),
        password = up.passwordUp.getOrElse(clientLoad.password)
      )
    )
  }
}

final case class ClientId(value: Long) extends AnyVal

final case class ClientPostForm(id: ClientId, name: String, email: String, password: String) {

  def asClientForm(id: ClientId): Client = Client(id, this.asClient)

  def asClient: ClientLoad = ClientLoad(name, email, password)

}

final case class ClientPatchForm(nameUp: Option[String] = None, emailUp: Option[String] = None, passwordUp: Option[String] = None)


object ClientPostForm {
  implicit val decoder: Decoder[ClientPostForm] = deriveDecoder
}

object ClientPatchForm {
  implicit val decoder: Decoder[ClientPatchForm] = deriveDecoder
}