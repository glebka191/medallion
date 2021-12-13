package repository

import dm.{Client, ClientId, ClientPatchForm, ClientPostForm}
import zio.{Has, UIO, URIO, ZIO}

object ClientRepository extends Serializable{
  trait Service extends Serializable {

    def getAll: UIO[List[Client]]

    def getById(id: ClientId): UIO[Option[Client]]

    def delete(id: ClientId): UIO[Unit]

    def deleteAll: UIO[Unit]

    def create(clientForm: ClientPostForm): UIO[Client]

    def update(id: ClientId, clientPatchForm: ClientPatchForm): UIO[Option[Client]]
  }

  val getAll: URIO[Has[ClientRepository.Service], List[Client]] =
    ZIO.accessM(_.get.getAll)

  def getById(id: ClientId): URIO[Has[ClientRepository.Service], Option[Client]] = ZIO.accessM(_.get.getById(id))

  def delete(id: ClientId): URIO[Has[ClientRepository.Service], Unit] = ZIO.accessM(_.get.delete(id))

  val deleteAll: URIO[Has[ClientRepository.Service], Unit] =
    ZIO.accessM(_.get.deleteAll)

  def create(clientForm: ClientPostForm): URIO[Has[ClientRepository.Service], Client] = ZIO.accessM(_.get.create(clientForm))

  def update(
              id: ClientId,
              clientPatchForm: ClientPatchForm
              ): URIO[Has[ClientRepository.Service], Option[Client]] = ZIO.accessM(_.get.update(id, clientPatchForm))

}