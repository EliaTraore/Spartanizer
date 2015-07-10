package il.ac.technion.cs.ssdl.spartan.refactoring;

import static il.ac.technion.cs.ssdl.spartan.refactoring.TESTUtils.asExpression;
import static il.ac.technion.cs.ssdl.spartan.refactoring.TESTUtils.assertSimplifiesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * * Unit tests for the nesting class Unit test for the containing class. Note
 * our naming convention: a) test methods do not use the redundant "test"
 * prefix. b) test methods begin with the name of the method they check.
 *
 * @author Yossi Gil
 * @since 2014-07-10
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
@SuppressWarnings({ "static-method", "javadoc" }) //
public class SimplificationEngineTestChainExpressions {
  @Test public void shorterChainParenthesisComparison() {
    assertSimplifiesTo("a == b == c", "c == (a == b)");
  }
  @Test public void chainComparison() {
    assertSimplifiesTo("a == true == b == c", "a == b == c");
  }
  @Test public void chainComparison0() {
    final InfixExpression e = asExpression("a == true == b == c");
    assertEquals("c", e.getRightOperand().toString());
    final Simplifier s = Simplifier.find(e);
    assertEquals(s, Simplifier.shortestOperandFirst);
    assertNotNull(s);
    assertTrue(s.withinScope(e));
    assertTrue(s.eligible(e));
    final Expression replacement = s.replacement(ASTRewrite.create(e.getAST()), e);
    assertNotNull(replacement);
    assertEquals("a == b == c", replacement);
  }
  @Test public void longChainComparison() {
    assertSimplifiesTo("a == b == c == d", "a == b == c == d");
  }
  @Test public void longChainParenthesisComparison() {
    assertSimplifiesTo("(a == b == c) == d", "d == (a == b == c)");
  }
  @Test public void longChainParenthesisNotComparison() {
    assertSimplifiesTo("(a == b == c) != d", "d != (a == b == c )");
  }
  @Test public void longerChainParenthesisComparison() {
    assertSimplifiesTo("(a == b == c == d == e) == d", "d == (a == b == c == d == e)");
  }
}