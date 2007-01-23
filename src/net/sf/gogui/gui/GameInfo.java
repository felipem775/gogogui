//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gui;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.sf.gogui.game.ConstClock;
import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.Clock;
import net.sf.gogui.game.Game;
import net.sf.gogui.game.NodeUtil;
import net.sf.gogui.go.ConstBoard;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.Move;

/** Panel displaying information about the current position. */
public class GameInfo
    extends JPanel
{
    public GameInfo(Game game)
    {
        super(new GridLayout(0, 2, GuiUtil.SMALL_PAD, GuiUtil.SMALL_PAD));
        m_game = game;
        m_captB = addEntry("Captured Black:");
        m_captW = addEntry("Captured White:");
        m_timeB = addEntry("Time Black:");
        m_timeW = addEntry("Time White:");
        m_timeB.setText("00:00");
        m_timeW.setText("00:00");
        Clock.Listener listener = new Clock.Listener() {
                public void clockChanged(ConstClock clock)
                {
                    SwingUtilities.invokeLater(m_updateTime);
                }
            };
        game.setClockListener(listener);
    }

    public void update(ConstNode node, ConstBoard board)
    {
        int capturedB = board.getCapturedB();
        if (capturedB == 0)
            m_captB.setText("");
        else
            m_captB.setText(Integer.toString(capturedB));
        int capturedW = board.getCapturedW();
        if (capturedW == 0)
            m_captW.setText("");
        else
            m_captW.setText(Integer.toString(capturedW));
        // Usually time left information is stored in a node only for the
        // player who moved, so we check the father node too
        ConstNode father = node.getFatherConst();
        if (father != null)
            updateTimeFromNode(father);
        updateTimeFromNode(node);
    }

    public void updateTimeFromClock(ConstClock clock)
    {
        updateTimeFromClock(clock, GoColor.BLACK);
        updateTimeFromClock(clock, GoColor.WHITE);
    }

    private class UpdateTimeRunnable
        implements Runnable
    {
        public void run()
        {
            updateTimeFromClock(m_game.getClock());
        }
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for serialver.sourceforge.net
    */
    private static final long serialVersionUID = 0L; // SUID

    private final JTextField m_captB;

    private final JTextField m_captW;

    private final JTextField m_timeB;

    private final JTextField m_timeW;

    private final Game m_game;

    private final UpdateTimeRunnable m_updateTime = new UpdateTimeRunnable();

    private JTextField addEntry(String text)
    {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        add(label);
        JTextField entry = new JTextField(" ");
        entry.setHorizontalAlignment(SwingConstants.LEFT);
        entry.setEditable(false);
        entry.setFocusable(false);
        add(entry);
        return entry;
    }

    private void updateTimeFromClock(ConstClock clock, GoColor color)
    {
        String text = clock.getTimeString(color);
        if (text == null)
            text = " ";
        if (color == GoColor.BLACK)
            m_timeB.setText(text);
        else
            m_timeW.setText(text);
    }

    private void updateTimeFromNode(ConstNode node)
    {
        double timeLeftBlack = node.getTimeLeft(GoColor.BLACK);
        int movesLeftBlack = node.getMovesLeft(GoColor.BLACK);
        if (! Double.isNaN(timeLeftBlack))
            m_timeB.setText(Clock.getTimeString(timeLeftBlack,
                                                movesLeftBlack));
        double timeLeftWhite = node.getTimeLeft(GoColor.WHITE);
        int movesLeftWhite = node.getMovesLeft(GoColor.WHITE);
        if (! Double.isNaN(timeLeftWhite))
            m_timeW.setText(Clock.getTimeString(timeLeftWhite,
                                                movesLeftWhite));
    }
}

