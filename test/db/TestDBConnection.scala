package db

import scalikejdbc.ConnectionPool

trait TestDBConnection {

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

}
