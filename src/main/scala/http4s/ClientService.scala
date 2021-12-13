package http4s

import dm.ClientPostForm.decoder
import dm.{ClientId, ClientPatchForm, ClientPostForm}
import io.circe.{Decoder, Encoder}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio._
import zio.interop.catz._
import repository.ClientRepository


object ClientService {

  def routes[R <: Has[ClientRepository.Service]](rootUri: String): HttpRoutes[RIO[R, *]] = {
    type ClientTask[A] = RIO[R, A]

    val dsl: Http4sDsl[ClientTask] = Http4sDsl[ClientTask]

    import dsl._

    implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[ClientTask, A] = jsonOf[ClientTask, A]

    implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[ClientTask, A] = jsonEncoderOf[ClientTask, A]

    HttpRoutes.of[ClientTask] {
      case GET -> Root / LongVar(id) =>
        for {
          client     <- ClientRepository.getById(ClientId(id))
          response <- client.fold(NotFound())(x => Ok(ClientWithUri(rootUri, x)))
        } yield response

      case GET -> Root =>
        Ok(ClientRepository.getAll.map(_.map(ClientWithUri(rootUri, _))))

      case req @ POST -> Root =>
        req.decode[ClientPostForm] { clientForm =>
          ClientRepository
            .create(clientForm)
            .map(ClientWithUri(rootUri, _))
            .flatMap(Created(_))
        }

      case DELETE -> Root / LongVar(id) =>
        for {
          client   <- ClientRepository.getById(ClientId(id))
          result <- client
            .map(x => ClientRepository.delete(x.id))
            .fold(NotFound())(_.flatMap(Ok(_)))
        } yield result

      case DELETE -> Root =>
        ClientRepository.deleteAll *> Ok()

      case req @ PATCH -> Root / LongVar(id) =>
        req.decode[ClientPatchForm] { updateForm =>
          for {
            update   <- ClientRepository.update(ClientId(id), updateForm)
            response <- update.fold(NotFound())(x => Ok(ClientWithUri(rootUri, x)))
          } yield response
        }
    }

  }
}
