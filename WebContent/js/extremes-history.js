/**
 * Highstock plugin stores x-axis extremes changes in browser history and
 * enables usual browser navigation trough the chart extremes change history.
 * 
 * Uses browser History API
 *
 * Author: Oleg Skydan
 * Email: sov1178@gmail.com
 *
 * Usage: add 'history' section to the options to change the default behaviour of the plugin
 *    opInterval - if time interval between consecutive extreme changes is less then opInterval they 
 *                 will be stored as a single record in the history. Default value is 3000 (3 seconds)
 * 
 */

(function (H) {

   var options = 
   {
      opInterval: 3000
   };

   H.Chart.prototype.callbacks.push(function (chart) 
   {

      //merge user options with the default ones
      if(chart.options.history)
      {
         H.merge(true, options, chart.options.history);
      }

      //add initializing code through the 'chart.load' event
      H.addEvent(chart, 'load', function (e) 
      {
         //check if History API is available
         if (window.history && window.history.pushState) 
         {
            //push the initial chart state
            var e = chart.xAxis[0].getExtremes();
            window.history.pushState({ time: 0, min: e.min, max: e.max }, "");

            //back-forward event handler
            $(window).on("popstate", function (event) 
            {
               if (event.originalEvent.state) 
               {
                  chart.xAxis[0].setExtremes(event.originalEvent.state.min, event.originalEvent.state.max, true, false);
               }
            });
         }
      });


      H.addEvent(chart.xAxis[0], 'afterSetExtremes', function (e) 
      {
         //use History API to navigate through the chart
         if (e.trigger != null && window.history && window.history.pushState) 
         {
            //fired only if extremes changed by the user action (not autoupdate or simple setExtremes call) 
            var l = window.history.state;
            var t = Date.now();
            if (l != null && t - l.time < options.opInterval) 
            {
               //If last changes were made within history.opInterval interval just update last history record
               window.history.replaceState({ time: t, min: e.min, max: e.max }, "");
            }
            else 
            {
               //Add new record otherwise
               window.history.pushState({ time: t, min: e.min, max: e.max }, "");
            }
         }
      });

   });

}(Highcharts));
