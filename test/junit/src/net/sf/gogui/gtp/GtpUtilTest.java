//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gtp;

import java.util.ArrayList;
import net.sf.gogui.game.TimeSettings;
import net.sf.gogui.go.GoPoint;

public final class GtpUtilTest
    extends junit.framework.TestCase
{
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite()
    {
        return new junit.framework.TestSuite(GtpUtilTest.class);
    }

    public void testParsePointString() throws GtpError
    {
        String s = " A1 b2\n textC3 C3 PASS text\tpass";
        ArrayList points = GtpUtil.parsePointString(s, 19);
        assertEquals(5, points.size());
        assertSame(GoPoint.get(0, 0), points.get(0));
        assertSame(GoPoint.get(1, 1), points.get(1));
        assertSame(GoPoint.get(2, 2), points.get(2));
        assertSame(null, points.get(3));
        assertSame(null, points.get(4));
    }

    public void getTimeSettingsCommand()
    {
        TimeSettings settings = new TimeSettings(60000);
        assertEquals(GtpUtil.getTimeSettingsCommand(settings),
                     "time_settings 60 0 0");
        settings = new TimeSettings(0, 60000, 10);
        assertEquals(GtpUtil.getTimeSettingsCommand(settings),
                     "time_settings 0 60 10");
        settings = new TimeSettings(30000, 60000, 10);
        assertEquals(GtpUtil.getTimeSettingsCommand(settings),
                     "time_settings 30 60 10");
    }
}

