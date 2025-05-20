package com.itv.cacti.pokemon

import cats.effect.IO
import cats.effect.kernel.Outcome
import cats.effect.unsafe.implicits.global
import cats.implicits.catsSyntaxTuple2Parallel

import java.util.concurrent.Executors
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

object SmallTask1 {

  /** A * Please watch this talk , it it VERY good. It will give you good
    * understanding of what IO is
    *
    * https://www.youtube.com/watch?v=g_jP47HFpWA
    *
    * B When running our Main class we get the run method out of the box thanks
    * to extend IOApp This makes our App to run within the IO context so our
    * whole app becomes one big IO combined out of other IO's
    *
    * This abstracts over one very important thing - the thread execution pool!
    * In Java we have access to Thread class that lets us interact directly with
    * underlying threads in our CPU IO need's access to those threads so it can
    * execute on them So in order to run IO in isolation we need to provide some
    * threads to it so it can utilise them for its execution
    *
    * This task will be a journey to understanding IO and concurrency in Scala
    * better
    */

  def main(args: Array[String]): Unit = {

    /**
     *
     * In Scala, scala.concurrent.Future is the standard way to perform asynchronous (non-blocking) computations.
     * It’s ideal for concurrency, but it’s not purely functional.
     * The problem is that its evaluation is eager meaning that it will be evaluated imminently
     * Which make it impossible to compose. In ideal world we want to describe our computation and run them only when
     * needed , not when we describe them
     *
     * */

      /**
       * Lets build a Future !
       *
       * final def apply[T](body: => T)(implicit executor: ExecutionContext): Future[T]
       *
       * Mean we can build a Future that will wrap over some result type T and we need implicit ExecutionContext in scope
       * Notice that the argument in the body is evaluated by reference so body will be evaluated ONLY when it is referred
       * aka it is lazy evaluated.
       * */


      /**
       * Lets provide the implicit we need in scope to be able to build our Future
       * */
      implicit val myExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

    /**
     * This mean that we are building execution context that will operate on fixed amount of threads , so we will run our async computations
     * on no more thn 5 threads
     *
     * Uncomment Block 1 and run the code to see whats happens
     * */


//    // Block 1
//    // Create a Future
//    val myFuture: Future[Int] = Future {
//      println(s"Running a computation on ${Thread.currentThread()}")
//      println()
//      Thread.sleep(1000) // simulate some work
//      42
//    }
//
//    val result1: Option[Try[Int]] = myFuture.value
//    println("Result of the computation now ")
//    println(result1)
//    println()
//
//    Thread.sleep(2000)
//
//    val result2: Option[Try[Int]] = myFuture.value
//    println("Result of the computation after 2000 milis")
//    println(result2)
//    println()


    /**
     *
     * That' pretty cool but as you might notice the same future has different values depending on the moment in time
     * and this sucks because we cannot make assumption on what the value is , we need to take into consideration
     * that the computation might fail. Thats why the results are of type Option[Try[Int]]
     *
     * We have Try because the computation that Future wraps over might fail
     * We have Option because the Future completed the computation or not
     *
     * Now lets try to Combine future computation together! Comment out Block 1 and uncomment Block 2
     * */


//      // Block 2
//    val computation = for {
//      a <- Future {
//        println("Computing first Number ...")
//        Thread.sleep(1000) // simulate some work
//        println()
//        5
//      }
//      b <- Future {
//        println("Computing second Number ...")
//        Thread.sleep(1000) // simulate some work
//        println()
//        5
//      }
//    } yield a + b
//
//    Thread.sleep(3000) // simulate some work
//
//    println(computation.value)


    /**
     *
     * We can see that the future evaluation happens in order because they are create in order. Can we run them at the same time ?
     *
     * Sure!
     *
     * NOTE ! We can put Future in the for comprehension because .flatMap exist on a future.
     * Comment out Block 2 and uncomment Block 3 , we will do it after each demonstration
     * */


//      // Block 3
//    val a = Future {
//        println("A")
//        Thread.sleep(1000)
//        1
//    }
//    val b = Future {
//      println("B")
//      Thread.sleep(1000)
//      2
//    }
//
//    val combined = for {
//      x <- a
//      y <- b
//    } yield x + y
//
//
//    Thread.sleep(3000) // simulate some work
//
//    println(combined.value)

    /**
     *
     * Now we can see that the Future a and b were running pretty much at the same time
     * The main take away is that Future runs at the moment of creation
     *
     * Task 1 - Create your own Future and explore the API it has
     * */



    /**
     *
     * Summary:
     *
     * Although Futures look nice at first, they have key drawbacks for functional I/O:
     *
     * 1.Eager evaluation: Side effects start immediately.
     * 2.No control over timing: You can’t defer execution.
     * 3.Difficult to test: You can’t "mock" time easily.
     * 4.Poor for resource safety: Hard to manage files, sockets, etc.
     * 5.Non-referential transparency: The same Future { println("Hello") } logs every time.
     *
     * */




    /**
     *
     * Its time to ditch Future and see that IO can do for us!
     *
     * Just as future IO needs some execution context to run on BUT! Because IO is not eagerly evaluated we need it only
     * when we want to run the IO. The context for IO is different than the one needed for the Future , for IO we need
     *
     * implicit instance of IORuntime . It is not so easy to build it from scratch so lets just import a global one with
     *
     * import cats.effect.unsafe.implicits.global
     *
     *
     * */

//    // Block 4
//
//    val myIO = IO{
//      println("Doing some computation ... ")
//      Thread.sleep(1000)
//      42
//    }
//
//    println("Waiting some time ...")
//    Thread.sleep(2000)
//    println(myIO.unsafeRunSync())

    /**
     *
     * That's better! We described the computation but run it only when calling unsafeRunSync()! This is the method that
     * actually makes triggers the computation of IO!
     *
     * No more eager evaluation! Lets try to compose some IO together!
     *
     * */

//    // Block 5
//    val myIO_1 = IO {
//      println("Computing first number ... ")
//      Thread.sleep(1000)
//      5
//    }
//    val myIO_2 = IO {
//      println("Computing second number ... ")
//      Thread.sleep(1000)
//      5
//    }
//
//    val result = for {
//      a <- myIO_1
//      b <- myIO_2
//    } yield a + b
//
//    println(result.unsafeRunSync())


    /**
     *
     * Easy! We managed to run one IO after another and combine the return values creating another IO!
     *
     * But can we run both at the same time as we did with Future? Sure!
     *
     * But this bring something new to the table
     *
     * A Fiber!
     *
     * A Fiber in Cats Effect is a lightweight, cancellable, and composable thread of execution—but not a real OS thread.
     * Think of it as a logical unit of concurrency managed by the Cats Effect runtime.
     *
     * A Fiber[IO, A] represents a computation that is:
     *
     * Running concurrently (in the background)
     * Will eventually return a result of type A
     * Can be joined to get the result
     * Can be canceled if you no longer need it
     *
     * Analogy:
     * A thread is like a heavyweight worker hired to do a task. Expensive and limited in number.
     *
     * A fiber is like a lightweight helper you can spawn cheaply and in large numbers.
     *
     * You don't need to worry about Fiber at this time , you will use them only when trying to run multiple IO in the same time
     * I am just showing it to you for the purpose of comparison between Future and IO
     *
     * */


//      // Block 6
//    val myIO_1 = IO {
//      println("Computing first number ... ")
//      Thread.sleep(1000)
//      5
//    }
//    val myIO_2 = IO {
//      println("Computing second number ... ")
//      Thread.sleep(1000)
//      5
//    }
//
//    val result = for {
//      fiber1 <- myIO_1.start // Turn the IO into Fiber so it runs in the background
//      fiber2 <- myIO_2.start
//
//      outcome1 <- fiber1.join // Wait for fiber to finish
//      outcome2 <- fiber2.join
//
//      // Just as in the Future , running the IO concurrently can fail , succeed or even be canceled!
//      // That's why our .join operation returns a type that reflects that , lets account for that possibility
//      // By matching on outcome and using exhaustive match to see all the variants of the outcome
//
//      a <- outcome1 match {
//        case Outcome.Succeeded(fa) => fa
//        case Outcome.Errored(e) => IO.raiseError(new RuntimeException(s"fiber1 failed with ${e}"))
//        case Outcome.Canceled() => IO.raiseError(new RuntimeException("fiber1 was canceled"))
//      }
//
//      b <- outcome2 match {
//        case Outcome.Succeeded(fa) => fa
//        case Outcome.Errored(e) => IO.raiseError(new RuntimeException(s"fiber2 failed with ${e}"))
//        case Outcome.Canceled() => IO.raiseError(new RuntimeException("fiber2 was canceled"))
//      }
//    } yield a + b
//
//
//    Thread.sleep(3000) // simulate some work
//
//    println(result.unsafeRunSync())


    /**
     *
     * Neat huh? But its kind of boilerplatey isn't it ? That's why we have useful abstractions over that behaviour
     * This example is the low level approach , lets explore some of the API's !
     * */

//      // Block 7
//    val myIO_1 = IO {
//      println("Computing first number ... ")
//      Thread.sleep(1000)
//      5
//    }
//    val myIO_2 = IO {
//      println("Computing second number ... ")
//      Thread.sleep(1000)
//      5
//    }
//
//    // Put both IO's in the tuple
//    // Map over both at the same time
//    // do it in Parallel
//    val result = (myIO_1, myIO_2).parMapN(_ + _)
//
//    Thread.sleep(3000) // simulate some work
//
//    println(result.unsafeRunSync())

    /**
     *
     * This is part of the power of IO! Look how much we can do and how much control we have!
     *
     * Because we can also cancel a Fiber mid execution we can do stuff like a race between to IO's!
     *
     * We can say , run two IO's in parallel but return the result from the first one to complete!
     * */

//    // Block 8
//    val myIO_1 = IO {
//      println("Computing first number ... ")
//      Thread.sleep(1000)
//      42
//    }
//    val myIO_2 = IO {
//      println("Computing second number ... ")
//      Thread.sleep(1500)
//      69
//    }
//
//    val raceResult: IO[Int] = myIO_1.race(myIO_2).map {
//      case Left(a) => a // myIO_1 won the race
//      case Right(b) => b // myIO_2 won the race
//    }
//
//    println(raceResult.unsafeRunSync())


    /**
     *
     * I've mentioned that IO can fail right? Lets explore that as well!
     * */

//      // Block 9
//    val myIO_1: IO[Int] = IO {
//      println("Computing first number ... ")
//      Thread.sleep(1000)
//      5
//    }
//
//    // IO that should return an Int but actually fails
//    val myIO_2: IO[Int] = IO.raiseError[Int](new Throwable("Dang!"))
//
//
//    val result = for {
//      a <- myIO_1
//      b <- myIO_2
//    } yield a + b
//
//    println(result.unsafeRunSync())

    /**
     *
     * That breaks our app! Not Good , can we do something about it ? Sure!
     *
     * We can catch the throwable and do something if it happens!
     * */

//    // Block 10
//    val myIO_1: IO[Int] = IO {
//      println("Computing first number ... ")
//      Thread.sleep(1000)
//      5
//    }
//
//    // IO that should return an Int but actually fails
//    val myIO_2: IO[Int] = IO.raiseError[Int](new Throwable("Dang!"))
//
//
//    val result = for {
//      a <- myIO_1
//      b <- myIO_2
//    } yield a + b
//
//    val safeResult = result.handleErrorWith(error => IO {
//      println(s"Got error = ${error}  but its cool! ")
//      Thread.sleep(1000)
//      5
//    })
//
//    println(safeResult.unsafeRunSync())


    /**
     *
     * Hmm , the return of safeResult is 5 , we were hoping for 10.
     * This happened because we handled error behind whole computation behind result val
     * Can we do better? Sure! We can handle errors directly in the for comprehension
     * because handleWrrorWith still returns an IO! So everything on the right hand side is
     * within the same context and we are golden!
     * */

    // Block 10
    val myIO_1: IO[Int] = IO {
      println("Computing first number ... ")
      Thread.sleep(1000)
      5
    }

    // IO that should return an Int but actually fails
    val myIO_2: IO[Int] = IO.raiseError[Int](new Throwable("Dang!"))


    val result = for {
      a <- myIO_1
      b <- myIO_2.handleErrorWith(error => IO {
        println(s"Got error = ${error}  but its cool! ")
        Thread.sleep(1000)
        5
      })
    } yield a + b


    println(result.unsafeRunSync())


  }




}
