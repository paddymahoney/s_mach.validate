/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      s_mach.validate
         .t1i .,::;;; ;1tt        Copyright (c) 2014 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate

/**
 * Various common text validators
 */
object Text {
  val nonEmpty =
    Validator.ensure[String](
      "must not be empty"
    )(_.nonEmpty)

  def maxLength(maxLength: Int) =
    Validator.ensure[String](
      s"must not be longer than $maxLength characters"
    )(_.length <= maxLength)

  val allLetters =
    Validator.ensure[String](
      "must contain only letters"
    )(_.forall(_.isLetter))

  val allDigits =
    Validator.ensure[String](
      "must contain only digits"
    )(_.forall(_.isDigit))

  val allLettersOrDigits =
    Validator.ensure[String](
      "must contain only letters or digits"
    )(_.forall(_.isLetterOrDigit))

  val allLettersOrSpaces =
    Validator.ensure[String](
      "must contain only letters or spaces"
    )(_.forall(c => c.isLetter || c.isSpaceChar))

  val allLettersDigitsOrSpaces =
    Validator.ensure[String](
      "must contain only letters, digits or spaces"
    )(_.forall(c => c.isLetterOrDigit || c.isSpaceChar))

  val base64UrlSafeRegex = "[A-Za-z0-9-_]*".r
  val isBase64UrlSafe =
    Validator.ensure[String](
      "must be a base 64 url safe value ([A-Za-z0-9-_]*)"
    )(s => base64UrlSafeRegex.findFirstIn(s).nonEmpty)
}