#system constants
radius = 20
grav   = .3

#system dynamics
equ = 600
pos = equ
vel = 0
acc = grav
err = equ - pos

#PID
kp = 1
ki = 0.025
kd = 7
inp = 0.0

#Deriviative and Integral
err_sum = 0
err_prev = 0

#FX
pos_array = []
p_array = []
i_array = []
d_array = []


def setup():
    size(800,800)
    frameRate(120)

def draw():
    global kp,ki,kd,inp,err,err_sum,err_prev,pos_array,p_array,i_array,d_array
    background(0)
    err = equ - pos
    err_del = err - err_prev
    prop = 0
    sumr = 0
    chan = 0
    
    if equ < height/12:
        err = 0
        inp = 0
    else:
        err_sum += err
        prop = kp*err
        sumr = ki*err_sum
        chan = kd*err_del
        inp = (prop + sumr + chan)/100
    
    drawGraph(pos,pos_array,0,color(255,0,255))
    drawGraph(prop/3,p_array,height/4,color(255,0,0))
    drawGraph(sumr/3,i_array,height/4,color(0,255,0))
    drawGraph(chan/3,d_array,height/4,color(0,0,255))
    physics()
    animate()
    userControl()
    err_prev = err
        
def physics():
    global pos,vel,acc,inp
    
    acc = grav + inp
    vel += acc
    pos += vel
    if pos > height - radius:
        vel *= -1
        pos = height - radius 

def drawGraph(val, val_arr, ofs, col):
    #Draw the position animation
    if len(val_arr) >= width/2:
        val_arr.pop()

    val_arr.insert(0,val)
    
    stroke(255)
    line(0,ofs,width,ofs)
    
    for i in range(len(val_arr)-1):
        stroke(col)
        #point(width/2 - i,pos_array[i])
        line(width/2 - i, ofs + val_arr[i], width/2 - (i+1),ofs + val_arr[i+1])
    
    
def animate():
    global pos, radius, equ
    stroke(255)
    fill(255)
    ellipse(width/2,pos,2*radius,2*radius)
    stroke(255)
    line(0,equ,width,equ)
    
def userControl():
    global ki,kp,kd,equ,grav
    
    bound = 40
    c = 30
    
    #Draw G
    textSize(32)
    fill(255,255,0)
    text("g", width / 5 - c, height / 12)
    fill(255)
    text(("%04.3f" % grav),  width / 5 - c - 13, height / 8)
    #Draw KP
    textSize(32)
    fill(255,0,0)
    text("kp", 2* width / 5 - c, height / 12)
    fill(255)
    text(("%04.3f" % kp), 2* width / 5 - c - 13, height / 8)
    #Draw KI
    textSize(32)
    fill(0,255,0)
    text("ki", 3* width / 5 - c, height / 12)
    fill(255)
    text(("%04.3f" % ki), 3* width / 5 - c - 13, height / 8)
    #Draw  KD
    textSize(32)
    fill(0,0,255)
    text("kd", 4 * width / 5 - c, height / 12)
    fill(255)
    text(("%04.3f" % kd), 4 *  width / 5 - c - 13, height / 8)
    #Change G
    if mousePressed and ((((mouseX > width / 5 - c - bound) and mouseX <  width / 5 - c + bound) and ((mouseY > height/10 - 2*bound) and mouseY < height/10 + 2*bound))):
        if mouseY < height/12 + 10:
            grav += .1*grav
        else:
            grav -= .1*grav
    #Change KP
    elif mousePressed and ((((mouseX > 2* width / 5 - c - bound) and mouseX <  2*width / 5 - c + bound) and ((mouseY > height/10 - 2*bound) and mouseY < height/10 + 2*bound))):
        if mouseY < height/12 + 10:
            kp += .1*kp
        else:
            kp -= .1*kp
    #Change KI
    elif mousePressed and ((((mouseX > 3* width / 5 - bound) and mouseX < 3* width / 5 + bound) and ((mouseY > height/10 - 2*bound) and mouseY < height/10 + 2*bound))):
        if mouseY < height/12 + 10:
            ki += .1*ki
        else:
            ki -= .1*ki
    #Change KD
    elif mousePressed and ((((mouseX > 4* width / 5 - bound) and mouseX < 4* width / 5 + bound) and ((mouseY > height/10 - 2*bound) and mouseY < height/10 + 2*bound))):
        if mouseY < height/12 + 10:
            kd += .1*kd
        else:
            kd -= .1*kd
            
    elif mousePressed:
        equ = mouseY
