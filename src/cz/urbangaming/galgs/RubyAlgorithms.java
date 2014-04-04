package cz.urbangaming.galgs;

import java.util.List;

import org.ruboto.JRubyAdapter;
import org.ruboto.RubotoComponent;
import org.ruboto.ScriptInfo;
import org.ruboto.ScriptLoader;

import android.util.Log;
import android.util.Pair;
import cz.urbangaming.galgs.utils.Point2D;

/**
 * 
 * @author Michal Karm Babacek
 * @license GNU GPL 3.0
 * 
 */
public class RubyAlgorithms {

    // Ruby integration
    private final RubotoComponent rbotoComponent = new RubotoComponent() {
        final ScriptInfo scriptInfo = new ScriptInfo();

        @Override
        public ScriptInfo getScriptInfo() {
            return scriptInfo;
        }
    };

    /**
     * Our entry point to Ruby scripts :-)
     * 
     * @param points
     * @return
     */
    public Pair<List<Point2D>, Integer> manipulateSceneWithRuby(List<Point2D> points, String scriptMethod) {
        rbotoComponent.getScriptInfo().setRubyClassName("GalgAlgorithms");
        if (JRubyAdapter.isInitialized()) {
            if (rbotoComponent.getScriptInfo().isReadyToLoad()) {
                ScriptLoader.loadScript(rbotoComponent);
                Object rubyInstance = rbotoComponent.getScriptInfo().getRubyInstance();
                // Holy shit...this can't work :-)
                @SuppressWarnings("unchecked")
                Pair<List<Point2D>, Object> result = (Pair<List<Point2D>, Object>) JRubyAdapter.runRubyMethod(rubyInstance, scriptMethod, points);
                //TODO: Investigate why the hell Long appears here. It doesn't help to set the Pair<List<Point2D>, Integer>
                //      and it doesn't help to explicitly return Integer .to_i in the Ruby code... hmm.
                // Nasty workaround, here we go:
                Pair<List<Point2D>, Integer> resultFixed = new Pair<List<Point2D>, Integer>(result.first, Integer.valueOf(((Long) result.second).intValue()));
                Log.d(GAlg.DEBUG_TAG, "RUBY RESULT params: " + resultFixed);
                return resultFixed;
            } else {
                Log.d(GAlg.DEBUG_TAG, "RUBY RESULT scriptInfo is not ready to load.");
                return null;
            }
        } else {
            Log.d(GAlg.DEBUG_TAG, "RUBY RESULT JRubyAdapter is not initialized.");
            return null;
        }
    }
}
