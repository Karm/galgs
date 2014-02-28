require 'ruboto/base'
require 'ruboto/package'

java_import 'android.util.Pair'
java_import 'cz.urbangaming.galgs.utils.Point2D'
java_import 'android.opengl.GLES20'

# You get List<Point2D> points
# You return Pair<List<Point2D>, Integer>

class GalgAlgorithms
  def link_points(points)
    return Pair.new(points, GLES20::GL_LINE_LOOP)
  end

  def red_star(points)
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