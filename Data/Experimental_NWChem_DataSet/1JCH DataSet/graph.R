library(ggplot2)
library(gridBase)
library(gridExtra)

# ============================================================================

SP3 = read.csv("SP3.csv",header=TRUE)
summary(SP3)
head(SP3)

# Observed...Predicted
p1 <- ggplot(SP3, aes(x=Experimental_1JCH, y=Observed...Predicted))+ xlab("Observed (Hz)") + ylab("Observed - Predicted (Hz)") + geom_point(shape=3,size=5)+scale_size(guide="none") +theme(legend.position = "none")+ 
  theme(axis.line = element_line(),
  panel.background = element_blank(),
  axis.title=element_text(face="bold",size="16", color="black")) + 
  scale_y_continuous(limit = c(-5, +5)) + 
  geom_point()
ggsave("obs-pred vs obs_SP3.tiff", dpi= 500);

# Observed...Predicted_fit
q1 <- ggplot(SP3, aes(x=Experimental_1JCH, y=Observed...Predicted_fit))+ xlab("Observed (Hz)") + ylab("Observed - Predicted (Hz)") + geom_point(shape=3,size=5)+scale_size(guide="none") +theme(legend.position = "none")+ 
  theme(axis.line = element_line(),
  panel.background = element_blank(),
  axis.title=element_text(face="bold",size="16", color="black"))+ 
  scale_y_continuous(limit = c(-5, +5))+ 
  geom_point()
ggsave("obs-pred_fit vs obs_SP3.tiff", dpi= 500);



SP2 = read.csv("SP2.csv",header=TRUE)
summary(SP2)
head(SP2)

# Observed...Predicted
p2 <- ggplot(SP2, aes(x=Experimental_1JCH, y=Observed...Predicted))+ xlab("Observed (Hz)") + ylab("Observed - Predicted (Hz)") + geom_point(shape=3,size=5)+scale_size(guide="none") +theme(legend.position = "none")+ 
  theme(axis.line = element_line(),
  panel.background = element_blank(),
  axis.title=element_text(face="bold",size="16", color="black"))+ 
  scale_y_continuous(limit = c(-7.5, +6))+ 
  geom_point()
ggsave("obs-pred vs obs_SP2.tiff", dpi= 500);

# Observed...Predicted_fit
q2 <- ggplot(SP2, aes(x=Experimental_1JCH, y=Observed...Predicted_fit))+ xlab("Observed (Hz)") + ylab("Observed - Predicted (Hz)") + geom_point(shape=3,size=5)+scale_size(guide="none") +theme(legend.position = "none")+ 
  theme(axis.line = element_line(),
  panel.background = element_blank(),
  axis.title=element_text(face="bold",size="16", color="black"))+ 
  scale_y_continuous(limit = c(-7.5, +6))+ 
  geom_point()
ggsave("obs-pred_fit vs obs_SP2.tiff", dpi= 500);



SP1 = read.csv("SP.csv",header=TRUE)
summary(SP1)
head(SP1)

# Observed...Predicted
p3 <- ggplot(SP1, aes(x=Experimental_1JCH, y=Observed...Predicted))+ xlab("Observed (Hz)") + ylab("Observed - Predicted (Hz)") + geom_point(shape=3,size=5)+scale_size(guide="none") +theme(legend.position = "none")+ 
  theme(axis.line = element_line(),
  panel.background = element_blank(),
  axis.title=element_text(face="bold",size="16", color="black"))+ 
  scale_y_continuous(limit = c(-20, +10)) + 
  geom_point()
ggsave("obs-pred vs obs_SP1.tiff", dpi= 500);

# Observed...Predicted_fit
q3 <- ggplot(SP1, aes(x=Experimental_1JCH, y=Observed...Predicted_fit))+ xlab("Observed (Hz)") + ylab("Observed - Predicted (Hz)") + geom_point(shape=3,size=5)+scale_size(guide="none") +theme(legend.position = "none")+ 
  theme(axis.line = element_line(),
  panel.background = element_blank(),
  axis.title=element_text(face="bold",size="16", color="black"))+ 
  scale_y_continuous(limit = c(-20, +10))+ 
  geom_point()
ggsave("obs-pred_fit vs obs_SP1.tiff", dpi= 500);



png(file = "modelo1.png", width = 2272, height = 2392)
grid.arrange(p1,q1,p2,q2,p3,q3, ncol = 2)
dev.off()
