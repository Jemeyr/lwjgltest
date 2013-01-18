#version 150

out vec4 outColor;

in vec3 colorPS;

void main() 
{

	outColor = vec4(colorPS, 1.0);

}

