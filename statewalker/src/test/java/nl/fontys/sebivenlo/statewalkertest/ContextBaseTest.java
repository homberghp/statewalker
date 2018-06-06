package nl.fontys.sebivenlo.statewalkertest;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.fontys.sebivenlo.statewalker.ContextBase;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static nl.fontys.sebivenlo.statewalkertest.S.*;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public class ContextBaseTest {

    Context ctx;

    @Before
    public void setup() {
        ctx = new Context( S.class ).initialize()
                .setDebug( true );
    }

    @After
    public void tearDown() {
        ctx = null;
    }

    @Test
    public void constructorWorks() {
        System.out.println( "==== constructorWorks" );
        String ss = ctx.logicalState();
        System.out.println( "initial state = " + ss );
        assertEquals( "SI", ss );
    }

    @Test
    public void testE1() {
        System.out.println( "==== e1" );
        ctx.e1();
        assertEquals( "S1.S11", ctx.logicalState() );
    }

    @Test
    public void testE2() {
        System.out.println( "==== e2" );
        ctx.e2();
        assertEquals( "S2.S21", ctx.logicalState() );
    }

    @Test
    public void testE3() {
        System.out.println( "==== e3" );
        ctx.e1();
        ctx.setDebug( false );

        ctx.e3();
        assertEquals( "SI", ctx.logicalState() );
    }

    @Test
    public void testE4() {
        Logger ctxLogger = Logger.getLogger( ContextBase.class.getName() );
        MyHandler mh = new MyHandler();
        ctxLogger.addHandler( mh );
        ctxLogger.setLevel( Level.FINE );
        System.out.println( "==== e4" );
        ctx.e1();
        assertTrue( ctx.isDebug() );
        mh.flush();
        ctx.e4();
        assertEquals( "S1.S12", ctx.logicalState() );
        assertTrue("wrong message "+mh.lastMessage,mh.lastMessage.contains( "from"));
        assertTrue("state not found", mh.lastParams.contains( "S1.S11"));
        ctx.setDebug( false );
        assertFalse( ctx.isDebug() );
        ctx.e4();
        assertEquals( "S1.S12", ctx.logicalState() );
    }

    @Test
    public void testE5() {
        System.out.println( "==== e5" );
        ctx.e2();

        ctx.e5();
        assertEquals( "S1.S11", ctx.logicalState() );
    }

    @Test
    public void testE6() {
        System.out.println( "==== e6" );
        ctx.e2();
        assertEquals( "S2.S21", ctx.logicalState() );

        ctx.e6();
        assertEquals( "S2.S22.S221", ctx.logicalState() );

        ctx.e6();
        assertEquals( "S2.S21", ctx.logicalState() );
    }

    @Test
    public void testE7() {
        System.out.println( "==== e7" );
        ctx.e2();
        ctx.e6();
        assertEquals( "S2.S22.S221", ctx.logicalState() );

        ctx.e7();
        assertEquals( "S2.S22.S222", ctx.logicalState() );

    }

    @Test
    public void testE8() {
        System.out.println( "==== e8" );
        assertEquals( "SI", ctx.logicalState() );
        ctx.e8();
        assertEquals( "S3.S32.S321", ctx.logicalState() );
    }

    @Test
    public void testE11a() {
        System.out.println( "==== e11a" );
        ctx.e8();
        ctx.e10();
        assertEquals( "S3.S33.S331", ctx.logicalState() );
        ctx.e11();
        assertEquals( "S3.S33.S332", ctx.logicalState() );
        ctx.e11();
        assertEquals( "S3.S33.S331", ctx.logicalState() );

    }

    @Test
    public void testHistory() {
        System.out.println( "==== history" );
        ctx.e2();
        ctx.e6();
        ctx.e7();
        assertEquals( "S2.S22.S222", ctx.logicalState() );
        ctx.e6();
        ctx.e6();
        assertEquals( "S2.S22.S222", ctx.logicalState() );
    }

    @Test
    public void testS3() {
        System.out.println( "S3========" );
        ctx.e1();
        assertEquals( "S1.S11", ctx.logicalState() );
        ctx.e9();
        assertEquals( "S3.S31", ctx.logicalState() );
        ctx.e10();
        assertEquals( "S3.S32.S321", ctx.logicalState() );
        ctx.e12();
        assertEquals( "S1.S11", ctx.logicalState() );

        ctx.e9();
        assertEquals( "S3.S32.S321", ctx.logicalState() );

        ctx.e10();
        assertEquals( "S3.S33.S331", ctx.logicalState() );
        ctx.e11();
        assertEquals( "S3.S33.S332", ctx.logicalState() );
        ctx.e12();
        assertEquals( "S1.S11", ctx.logicalState() );
        ctx.e9();
        assertEquals( "S3.S33.S332", ctx.logicalState() );
        ctx.e10();
        ctx.e10();
        assertEquals( "S3.S32.S321", ctx.logicalState() );
        ctx.e12();
        ctx.e9();
        assertEquals( "S3.S32.S321", ctx.logicalState() );

    }

    @Test
    public void testDeepHistory() {
        System.out.println( "Deep history" );
        ctx.e8();
        assertEquals( "S3.S32.S321", ctx.logicalState() );
        ctx.e11();
        assertEquals( "S3.S32.S322", ctx.logicalState() );
        ctx.e12();
        ctx.e9();
        assertEquals( "S3.S32.S322", ctx.logicalState() );
    }

    @Test
    public void testE13() {
        ctx.e2();
        ctx.e6();
        ctx.e7();
        assertEquals( "S2.S22.S222", ctx.logicalState() );
        ctx.e13();
        assertEquals( "S1.S11", ctx.logicalState() );

    }

    @Test( expected = IllegalArgumentException.class )
    public void leavNonExistingSubStates() {
        ctx.e1();
        ctx.leaveSubStates( S21 );
    }

}
