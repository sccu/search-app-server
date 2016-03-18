package name.sccu

/**
  * Created by sccu on 2016. 3. 17..
  */
package utils {

  object alternatives {
    def apply[T](values: T*): T = {
      val itr = values.iterator
      while (itr.hasNext) {
        val value = itr.next()
        if (value != null) {
          return value
        }
      }
      null.asInstanceOf[T]
    }
  }

  object stopwatch {
    def apply[T](block: => T): (T, Long) = {
      val start = System.nanoTime()
      val ret = block
      val end = System.nanoTime()
      (ret, (end - start) / 1000000)
    }
  }
}
