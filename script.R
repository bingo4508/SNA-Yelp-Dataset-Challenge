library("rjson")
setwd('C:/Users/bingo4508/Desktop/SNA/Final/SNA-final/data')

load_json = function(fn){
  lines <- readLines(fn)  
  table = data.frame()
  for(i in 1:length(lines)){
    print(i)
    l = fromJSON(lines[i])
    t = c()
    for(e in l){
      if(typeof(e) != "list"){
        t = c(t, e)
      }
    }
    table = rbind(table, t)
  }
  table
}

business = load_json('Business.txt')
review = load_json('Review.txt')
user = load_json('User.txt')