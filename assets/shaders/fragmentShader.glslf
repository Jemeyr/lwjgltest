#version 150

out vec4 outColor;

uniform float triGreen;

void main() 
{
	outColor = vec4( 0.0, triGreen, 0.0, 1.0 );
};

