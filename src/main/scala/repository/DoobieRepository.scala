package repository

import cats.implicits._
import dm._
import doobie._
import doobie.free.connection
import doobie.implicits._
import doobie.util.transactor.Transactor
import zio._
import zio.interop.catz._


private class DoobieRepository(xa: Transactor[Task]) extends ClientRepository.Service {

  import repository.DoobieRepository.SqlQueries

  override def getAll: UIO[List[Client]] =
    SqlQueries.getAll
      .to[List]
      .transact(xa)
      .orDie

  override def getById(id: ClientId): UIO[Option[Client]] =
    SqlQueries.get(id)
      .option
      .transact(xa)
      .orDie

  override def delete(id: ClientId): UIO[Unit] =
    SqlQueries.delete(id)
      .run
      .transact(xa)
      .unit
      .orDie

  override def deleteAll: UIO[Unit] =
    SqlQueries.deleteAll.run
      .transact(xa)
      .unit
      .orDie

  override def create(clientForm: ClientPostForm): UIO[Client] =
    SqlQueries.create(clientForm.asClient)
      .withUniqueGeneratedKeys[Long]("ID")
      .map(id => clientForm.asClientForm(ClientId(id)))
      .transact(xa)
      .orDie

  override def update(id: ClientId, clientPatchForm: ClientPatchForm): UIO[Option[Client]] =
    (for {
      oldClient <- SqlQueries.get(id).option
      newClient  = oldClient.map(_.update(clientPatchForm))
      _       <- newClient.fold(connection.unit)(client => SqlQueries.update(client).run.void)
    } yield newClient)
      .transact(xa)
      .orDie
}
object DoobieRepository {
  object SqlQueries {
    def create(client: ClientLoad): Update0 =
      sql"""
      INSERT INTO CLIENTS (NAME, EMAIL, PASSWORD)
      VALUES (${client.name}, ${client.email}, ${client.password})
      """.update

    def get(id: ClientId): Query0[Client] = sql"""
      SELECT * FROM CLIENTS WHERE ID = ${id.value}
      """.query[Client]

    val getAll: Query0[Client] = sql"""
      SELECT * FROM CLIENTS
      """.query[Client]

    def delete(id: ClientId): Update0 =
      sql"""
      DELETE from CLIENTS WHERE ID = ${id.value}
      """.update

    val deleteAll: Update0 =
      sql"""
      DELETE from CLIENTS
      """.update

    def update(client: Client): Update0 =
      sql"""
      UPDATE CLIENTS SET
      NAME = ${client.clientLoad.name},
      EMAIL = ${client.clientLoad.email},
      PASSWORD = ${client.clientLoad.password}
      WHERE ID = ${client.id.value}
      """.update
  }
}


