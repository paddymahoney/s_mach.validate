package s_mach.validate

import scala.language.higherKinds
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.ClassTag
import s_mach.validate.impl._

/**
 * A type-class for validating instances of a type
 * @tparam A type validated
 */
trait Validator[A] {
  /**
   * Validate an instance
   * @param a instance to validate
   * @return list of rules that failed to validate or
   *         Nil if the instance passes all validation
   */
  def apply(a: A) : List[Rule]

  /** @return list of rules that this validator tests */
  def rules : List[Rule]

  /** @return schema for type A */
  def schema: Schema

  /** @return list of schema for child fields and their descendants */
  def descendantSchema: List[Schema]

  /** @return list of rules and all schema for type A */
  final def explain: List[Explain] =
    // Order here is important
    schema :: (descendantSchema ::: rules)

  /**
   * Compose two validators
   * @param other validtor to compose with this
   * @return a new validator composed of this and other
   */
  def and(other: Validator[A]) : Validator[A]
}

object Validator {
  def empty[A](implicit ca:ClassTag[A]) = new Validator[A] {
    def apply(a: A) = Nil
    def and(other: Validator[A]) = other
    def rules = Nil
    def descendantSchema = Nil
    val schema = Schema(Nil,ca.toString(),(1,1))
  }

  /**
   * Generate a DataDiff implementation for a product type
   * @tparam A the value type
   * @return the DataDiff implementation
   */
  def forProductType[A <: Product] : Validator[A] =
    macro macroForProductType[A]

  // Note: Scala requires this to be public
  def macroForProductType[A:c.WeakTypeTag](
    c: blackbox.Context
  ) : c.Expr[Validator[A]] = {
    val builder = new impl.ValidateMacroBuilderImpl(c)
    builder.build[A]().asInstanceOf[c.Expr[Validator[A]]]
  }

  /**
   *
   * @param other
   * @param va
   * @param vt
   * @tparam V
   * @tparam A
   * @return
   */
  def forValueType[V <: IsValueType[A],A](other: Validator[A])(implicit
    va:Validator[A],
    vt: ValueType[V,A],
    ca: ClassTag[A],
    cv: ClassTag[V]
  ) = ValueTypeValidator[V,A](
    va and other
  )

  /**
   * A validator that is composed of zero or more validators
   * @param validators composed validators
   * @tparam A type validated
   */
  def apply[A](
    validators: Validator[A]*
  )(implicit
    ca:ClassTag[A]
  ) : CompositeValidator[A] =
    CompositeValidator[A](validators.toList)

  /**
   * A validator that tests a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @tparam A type validated
   */
  def ensure[A](
    message: String
  )(
    f: A => Boolean
  )(implicit
    ca:ClassTag[A]
  ) =
    EnsureValidator[A](message,f)

  /**
   * A validator that adds a comment to rules
   * @param message comment
   * @tparam A type validated
   */
  def comment[A](message: String)(implicit ca:ClassTag[A]) =
    ExplainValidator[A](Rule(Nil,message))

  /**
   * A validator for an Option[A] that always passes if set to None
   * @param va the validator for A
   * @param ca class tag for A
   * @tparam A type validated
   */
  def optional[A](
    va: Validator[A]
  )(implicit
    ca:ClassTag[A]
  ) = OptionValidator[A](va)

  /**
   * A validator for a collection of A
   * @param va the validator for A
   * @param ca the class tag for A
   * @tparam M the collection type
   * @tparam A the type validated
   */
  def zeroOrMore[
    M[AA] <: Traversable[AA],
    A
  ](
    va: Validator[A]
  )(implicit
    ca:ClassTag[A],
    cm:ClassTag[M[A]]
  ) = CollectionValidator[M,A](va)
}
