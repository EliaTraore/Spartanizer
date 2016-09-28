package il.org.spartan.spartanizer.tippers;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.java.*;
import il.org.spartan.spartanizer.tipping.*;

/** Simplify if statements as much as possible (or remove them or parts of them)
 * if and only if </br>
 * it doesn't have any side-effect.
 * @author Dor Ma'ayan
 * @since 2016-09-26 */
public class RemoveRedundantWhile extends ReplaceCurrentNode<WhileStatement> implements Kind.Collapse {
  private static boolean checkVariableDecleration(VariableDeclarationStatement s) {
    for (VariableDeclarationFragment ¢ : step.fragments(s))
      if (¢.getInitializer() != null && haz.sideEffects(¢.getInitializer()))
        return false;
    return true;
  }

  private boolean checkBlock(ASTNode n) {
    if (n != null
        && (iz.expression(n) && haz.sideEffects(az.expression(n))
            || iz.expressionStatement(n) && haz.sideEffects(az.expressionStatement(n).getExpression())) //
        || !iz.block(n) && !iz.isVariableDeclarationStatement(n) //
        || (iz.variableDeclarationStatement(n) && !checkVariableDecleration(az.variableDeclrationStatement(n))))
      return false;
    if (iz.block(n))
      for (Statement ¢ : step.statements(az.block(n)))
        if (iz.expressionStatement(¢) && haz.sideEffects(az.expression(az.expressionStatement(¢).getExpression()))
            || !iz.isVariableDeclarationStatement(¢) || !checkVariableDecleration(az.variableDeclrationStatement(¢)))
          return false;
    return true;
  }

  @Override public ASTNode replacement(WhileStatement ¢) {
    return ¢ == null || haz.sideEffects(¢.getExpression()) || !checkBlock(¢.getBody()) ? null : ¢.getAST().newBlock();
  }

  @Override public String description(WhileStatement ¢) {
    return "remove :" + ¢;
  }
}