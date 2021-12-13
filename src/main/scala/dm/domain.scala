package dm

import io.circe.Decoder
import io.circe.generic.semiauto._


final case class Client(id: ClientId, name: String, email: String, password: String) {
  def update(up: ClientUpdate): Unit ={
    this.copy(
      name = up.nameUp.getOrElse(name),
      email = up.emailUp.getOrElse(email),
      password = up.passwordUp.getOrElse(password)
    )
  }
}

final case class ClientId(value: Long) extends AnyVal

final case class ClientUpdate(nameUp: Option[String] = None, emailUp: Option[String] = None, passwordUp: Option[String] = None)

final case class ClientPostForm(name: String, email: String, password: String) {
  def asTodoItem(id: ClientId): Client = Client(id, name, email, password)
}

final case class ClientPatchForm(name: String, email: String, password: String)


object ClientPostForm {
  implicit val decoder: Decoder[ClientPostForm] = deriveDecoder
}

object ClientPatchForm {
  implicit val decoder: Decoder[ClientPatchForm] = deriveDecoder
}