//----------------------------------------------------------------------------
// $Id$
// $Source$
//----------------------------------------------------------------------------

package net.sf.gogui.gui;

import javax.swing.SwingUtilities;
import net.sf.gogui.go.Board;

//----------------------------------------------------------------------------

/** Parse standard error for GoGui live graphics commands. */
public class LiveGfx
{
    public LiveGfx(Board board, GuiBoard guiBoard, StatusBar statusBar)
    {
        m_board = board;
        m_guiBoard = guiBoard;
        m_statusBar = statusBar;
        m_duringMultiLineResponse = false;
    }

    public void receivedStdErr(String s)
    {
        for (int i = 0; i < s.length(); ++i)
        {
            char c = s.charAt(i);
            if (c == '\r' || c == '\n')
            {
                if (SwingUtilities.isEventDispatchThread())
                    handleLine(m_buffer.toString());
                else
                    invokeAndWait(new Runnable()
                        {
                            public void run()
                            {
                                handleLine(m_buffer.toString());
                            }
                        });
                m_buffer.setLength(0);
            }
            else
                m_buffer.append(c);
        }
    }

    private boolean m_duringMultiLineResponse;

    private final StringBuffer m_buffer = new StringBuffer(1024);    

    private final StringBuffer m_response = new StringBuffer(1024);    

    private final Board m_board;

    private final GuiBoard m_guiBoard;

    private final StatusBar m_statusBar;

    private void handleLine(String s)
    {
        if (m_duringMultiLineResponse)
        {
            if (s.equals(""))
            {
                showGfx(m_response.toString());
                m_duringMultiLineResponse = false;
            }
            else
            {
                m_response.append(s);
                m_response.append('\n');
            }
            return;
        }
        s = s.trim();
        if (s.startsWith("gogui-gfx:"))
        {
            int pos = s.indexOf(':');
            String response = s.substring(pos + 1);
            if (response.trim().equals(""))
            {
                m_response.setLength(0);
                m_duringMultiLineResponse = true;
            }
            else
                showGfx(response);
        }
    }

    private void invokeAndWait(Runnable runnable)
    {
        try
        {
            SwingUtilities.invokeAndWait(runnable);
        }
        catch (InterruptedException e)
        {
            System.err.println("Thread interrupted");
        }
        catch (java.lang.reflect.InvocationTargetException e)
        {
            System.err.println("InvocationTargetException");
        }
    }

    private void showGfx(String text)
    {
        m_guiBoard.clearAll();
        GuiBoardUtils.updateFromGoBoard(m_guiBoard, m_board, false);
        String statusText = AnalyzeShow.showGfx(text, m_guiBoard);
        if (statusText != null)
            m_statusBar.setText(statusText);
    }
}

//----------------------------------------------------------------------------