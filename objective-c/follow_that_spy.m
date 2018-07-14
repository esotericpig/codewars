#import <Foundation/Foundation.h>

/**
 * @author Jonathan Bradley Whited (@esotericpig)
 * @see    https://www.codewars.com/kata/follow-that-spy/objc
 * @rank   6 kyu
 */
NSString* findRoutes(NSArray* routes) {
  NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
  
  for(id route in routes) {
    dict[route[0]] = route[1];
  }
  
  NSMutableArray* realRoutes = [[NSMutableArray alloc] init];
  
  for(id route in routes) {
    id from = route[0];
    id to = nil;
    
    [realRoutes addObject:from];
    
    while((to = dict[from]) != nil) {
      [realRoutes addObject:to];
      from = to;
      to = nil;
    }
    
    if([realRoutes count] != ([routes count] + 1)) {
      // Try again
      [realRoutes removeAllObjects];
    }
    else {
      break;
    }
  }
  
  NSString* realRoute = [realRoutes componentsJoinedByString:@", "];
  
  [dict release];
  [realRoutes release];
  
  return realRoute;
}

/*
// @"MNL, TAG, CEB, TAC, BOR"
findRoutes(@[@[@"MNL",@"TAG"],
             @[@"CEB",@"TAC"],
             @[@"TAG",@"CEB"],
             @[@"TAC",@"BOR"]]);

// @"Halifax, Montreal, Toronto, Chicago, Winnipeg, Seattle"
findRoutes(@[@[@"Chicago" ,@"Winnipeg"],
             @[@"Halifax" ,@"Montreal"],
             @[@"Montreal",@"Toronto"],
             @[@"Toronto" ,@"Chicago"],
             @[@"Winnipeg",@"Seattle"]]);

NSArray *args = [[NSProcessInfo processInfo] arguments];
for(id arg in args) {
  NSLog(@"%@", arg);
}
*/
