package il.org.spartan.spartanizer.dispatch;

import java.util.*;
import java.util.concurrent.atomic.*;

import javax.xml.bind.Marshaller.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.jface.text.*;
import org.eclipse.text.edits.*;

import il.org.spartan.plugin.*;
import il.org.spartan.spartanizer.cmdline.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.spartanizer.utils.*;

/** @author Yossi Gil
 * @since 2015/07/10 */
public class ForTestCompatabilityRewritePolicy extends AdviceGenerator {
  public static boolean prune(final Tip r, final List<Tip> rs) {
    if (r != null) {
      r.pruneIncluders(rs);
      rs.add(r);
    }
    return true;
  }
  public void makeAdvice(final ASTRewrite r, final CompilationUnit u, final IMarker m, final AtomicInteger i) {
      u.accept(new DispatchingVisitor() {
        @Override protected <N extends ASTNode> boolean go(final N n) {
          config.listeners().hit(n);
          if (!check(n) || !inRange(m, n) || disabling.on(n))
            return true;
          final Tipper<N> w = getTipper(n);
          if (w == null)
            return true;
          ShortTip s = null;
          try {
            s = w.tip(n, exclude);
            TrimmerLog.tip(w, n);
          } catch (final TipperFailure f) {
            monitor.debug(this, f);
          }
          if (s != null) {
            i.incrementAndGet();
          }
          return true;
        }
  
  
  
        @Override protected void initialization(final ASTNode ¢) {
          disabling.scan(¢);
        }
      });
    }
  public String fixed(final String from) {
    for (final Document $ = new Document(from);;) {
      final CompilationUnit u = (CompilationUnit) makeAST.COMPILATION_UNIT.from($.get());
      final ASTRewrite r = ASTRewrite.createRewrite(u);
      final TextEdit e = r.rewriteAST($, null);
      try {
        e.apply($);
      } catch (final MalformedTreeException | IllegalArgumentException | BadLocationException x) {
        monitor.logEvaluationError(this, x);
        throw new AssertionError(x);
      }
      if (!e.hasChildren())
        return $.get();
    }
  }
 ASTVisitor makeTipsCollector(final List<Tip> $) {
    return new DispatchingVisitor() {
      @Override protected <N extends ASTNode> boolean go(final N n) {
        config.listeners().hit(n);
        if (!check(n) || disabling.on(n))
          return true;
        final Tipper<N> w = config.toolbox.getTipper(n);
        if (w != null)
          config.listeners().select(n, w);
        try {
          return w == null || w.cantTip(n) || prune(w.tip(n, exclude), $);
        } catch (final TipperFailure f) {
          monitor.debug(this, f);
        }
        return false;
      }

      @Override protected void initialization(final ASTNode ¢) {
        disabling.scan(¢);
      }
    };
  }

  public abstract class With {
    public AdviceGenerator forTestCompatabilityRewritePolicy() {
      return ForTestCompatabilityRewritePolicy.this;
    }
  }

  @SuppressWarnings("static-method") protected <N extends ASTNode> boolean check(@SuppressWarnings("unused") final N __) {
    return true;
  }

  @SuppressWarnings("static-method") protected <N extends ASTNode> Tipper<N> getTipper(final N ¢) {
    return Toolbox.defaultInstance().firstTipper(¢);
  }
}