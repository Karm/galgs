require 'ruboto/base'
require 'ruboto/package'

java_import 'android.util.Pair'
java_import 'cz.urbangaming.galgs.utils.Point2D'
java_import 'android.opengl.GLES20'

class KarmTest
  def butterfly(points)
    return Pair.new(points, GLES20::GL_LINE_LOOP)
  end
end