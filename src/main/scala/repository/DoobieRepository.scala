package repository
import dm.{Client, ClientId, ClientPatchForm, ClientPostForm}
import zio.UIO

private class DoobieRepository extends ClientRepository.Service {

  override def getAll: UIO[List[Client]] = ???

  override def getById(id: ClientId): UIO[Option[Client]] = ???

  override def delete(id: ClientId): UIO[Unit] = ???

  override def deleteAll: UIO[Unit] = ???

  override def create(clientForm: ClientPostForm): UIO[Client] = ???

  override def update(id: ClientId, clientPatchForm: ClientPatchForm): UIO[Option[Client]] = ???
}
