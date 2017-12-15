package anorm

abstract class DAO[A] {
  
  val database: Database

  //-- Parsers
  val simple: RowParser[A]  

  val tableName: String

  //-- SQL
  private def whereString(params: Seq[NamedParameter]) = 
    if(params.length <= 1){
      params.map{ param =>
        s"${param.name}={${param.name}}"
      }.mkString("\n")
    } else {        
      params.init
        .map{ param =>
          s"${param.name}={${param.name}} AND "
        }.mkString("\n") + params.lastOption
                              .map{ param =>
                                s"${param.name}={${param.name}}"
                              }.mkString("\n")
    }

  def byId(id: Long) = whereSingle(Seq("id" -> id))

  def where(params: Seq[NamedParameter], offset:Int, limit: Int): List[A] = 
    database.withConnection { implicit conn =>
      SQL"""
        SELECT * FROM #$tableName         
        WHERE #${whereString(params)}
        LIMIT ${limit}
        OFFSET ${offset}
      """
        .on(params:_*)
        .as(simple *)
    }

  def whereSingle(params: Seq[NamedParameter]): Option[A] = 
    database.withConnection { implicit conn =>
      SQL"""
        SELECT * FROM #$tableName 
        WHERE #${whereString(params)}
        LIMIT 1
      """
        .on(params:_*)
        .as(simple.singleOpt)
    }

}