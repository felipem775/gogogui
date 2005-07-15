//----------------------------------------------------------------------------
// $Id$
// $Source$
//----------------------------------------------------------------------------

package net.sf.gogui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

//----------------------------------------------------------------------------

class RecentMenuItem
    extends JMenuItem
{
    public RecentMenuItem(String label, String value,
                          ActionListener listener)
    {
        super(label);
        m_label = label;
        m_value = value;
        addActionListener(listener);
    }

    public String getRecentMenuLabel()
    {
        return m_label;
    }

    public String getRecentMenuValue()
    {
        return m_value;
    }

    private String m_label;

    private String m_value;
}

//----------------------------------------------------------------------------

/** Menu for recent item.
    Handles removing duplicates and storing the items between sessions.
*/
public class RecentMenu
{
    public interface Callback
    {
        void itemSelected(String label, String value);
    }

    public RecentMenu(String label, File file, Callback callback)
    {
        assert(callback != null);
        assert(file != null);
        m_file = file;
        m_callback = callback;
        m_menu = new JMenu(label);
        m_listener = new ActionListener()
            {
                public void actionPerformed(ActionEvent event)
                {
                    RecentMenuItem item = (RecentMenuItem)event.getSource();
                    String label = item.getRecentMenuLabel();
                    String value = item.getRecentMenuValue();
                    m_callback.itemSelected(label, value);
                }
            };
        load();
    }

    public void add(String label, String value)
    {
        for (int i = 0; i < getCount(); ++i)
            if (getValue(i).equals(value))
                m_menu.remove(i);
        JMenuItem item = new RecentMenuItem(label, value, m_listener);
        m_menu.add(item, 0);
        while (getCount() > m_maxItems)
            m_menu.remove(getCount() - 1);
        save();
    }

    /** Don't modify the items in this menu! */
    public JMenu getMenu()
    {
        return m_menu;
    }

    private static class Item
    {
        public String m_label;

        public String m_value;
    }

    private final int m_maxItems = 20;

    private ActionListener m_listener;

    private Callback m_callback;

    private File m_file;

    private JMenu m_menu;

    private int getCount()
    {
        return m_menu.getItemCount();
    }

    private RecentMenuItem getItem(int i)
    {
        return (RecentMenuItem)m_menu.getItem(i);
    }

    private String getLabel(int i)
    {
        return getItem(i).getRecentMenuLabel();
    }

    private String getValue(int i)
    {
        return getItem(i).getRecentMenuValue();
    }

    private void load()
    {
        Properties props = new Properties();
        try
        {
            props.loadFromXML(new FileInputStream(m_file));
        }
        catch (IOException e)
        {
            return;
        }
        m_menu.removeAll();
        for (int i = 0; i < m_maxItems; ++i)
        {
            String label = props.getProperty("label_" + i);
            String value = props.getProperty("value_" + i);
            if (label == null || value == null)
                continue;
            add(label, value);
        }
    }

    private void save()
    {
        Properties props = new Properties();
        for (int i = 0; i < getCount(); ++i)
        {
            props.setProperty("label_" + i, getLabel(i));
            props.setProperty("value_" + i, getValue(i));
        }
        try
        {
            props.storeToXML(new FileOutputStream(m_file), null);
        }
        catch (IOException e)
        {
        }
    }
}

//----------------------------------------------------------------------------