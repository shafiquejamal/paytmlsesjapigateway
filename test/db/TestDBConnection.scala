package db

import user.WrappedResultSetToTestUserConverterImpl

trait TestDBConnection {

  val converter = new WrappedResultSetToTestUserConverterImpl()
  val dBConfig = new ScalikeJDBCTestDBConfig()

}
