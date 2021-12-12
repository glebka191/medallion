package http4s

import dm.ClientPostForm.decoder
import dm.{ClientId, ClientPostForm}
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
    }

  }
}
