package anorm.helpers

trait PaginationHelpers {

  def withOffset[A](page: Int, limit: Int, maxLimit: Int = 40)(block: (Int, Int) => A): A = {
    val l = if (limit > maxLimit) maxLimit else limit    
    block((l * page), limit)
  }

}