require 'ruboto/base'
require 'ruboto/package'

java_import 'android.util.Pair'
java_import 'cz.urbangaming.galgs.utils.Point2D'
java_import 'android.opengl.GLES20'

# You get List<Point2D> points
# You return Pair<List<Point2D>, Integer>

# The methods you want to have dynamically
# loaded into the GAlgs Application
# must contain "galgs_" as a prefix.
# Methods are parsed as: [ \\t]*def[ \\t]*galgs_([^(]*)\\(.*"

# Please, keep the method names reasonably long,
# like not more than 30 characters without the aforementioned prefix.

class GalgAlgorithms
  
  def galgs_link_points(points)
    return Pair.new(points, GLES20::GL_LINE_LOOP)
  end

  def galgs_star(points)
    result_points = []
    points.sort{ |a,b| (a.x == b.x) ? a.y <=> b.y : a.x <=> b.x }
    middle_one = points[points.length/2]
    points.each { |point|
      result_points.push(middle_one)
      result_points.push(point)
    }
    return Pair.new(result_points, GLES20::GL_LINES)
  end

end