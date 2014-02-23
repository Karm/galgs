require 'ruboto/widget'
require 'ruboto/util/toast'

ruboto_import_widgets :Button, :LinearLayout, :TextView

import 'android.os.Bundle'
import 'android.content.Intent'

class RubyActivity
  def onCreate(bundle)
    super
    set_title 'Domo arigato, Mr Ruboto!'

    self.content_view =
        linear_layout :orientation => :vertical do
          @text_view = text_view :text => 'What hath Matz wrought?', :id => 42, 
                                 :layout => {:width => :match_parent},
                                 :gravity => :center, :text_size => 48.0
          button :text => 'M-x butterfly', 
                 :layout => {:width => :match_parent},
                 :id => 43, :on_click_listener => proc { butterfly }
        end
  rescue Exception
    puts "Exception creating activity: #{$!}"
    puts $!.backtrace.join("\n")
  end

  private

  def butterfly
     conData = Bundle.new()
     conData.putString("results", "Thanks Thanks")
     intent = Intent.new()
     intent.putExtras(conData);
     setResult(RESULT_OK, intent);
     finish()
    #@text_view.text = 'What hath Matz wrought!'
    #toast 'Flipped a bit via butterfly'
  end

end
